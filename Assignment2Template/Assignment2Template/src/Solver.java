import java.util.*;

class Solver {
    static class Variable {
        public HashSet<Variable> connectedVariables;
        public SortedSet<Integer> domain;
        public Integer AssignedValue;
        private final Stack<SortedSet<Integer>> domainHistory;

        /**
         * Constructs a Variable with a specified domain.
         * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
         *     However, you are allowed to change its behavior in any way you want.
         *
         * @param domain A list of integers, representing the domain of the variable.
         */
        public Variable(List<Integer> domain) {
            this.domain = new TreeSet<>(domain);
            this.domainHistory = new Stack<>();
        }

        public Integer Min(){
            return domain.first();
        }

        public Integer Max(){ return domain.last(); }

        public void Assign(int x){
            AssignedValue = x;
        }

        public void Unassign(){
            AssignedValue = null;
        }

        public boolean isAssigned() {
            return AssignedValue != null;
        }

        public void saveDomain() {
            domainHistory.push(new TreeSet<>(domain));
        }

        public void restoreDomain() {
            if (!domainHistory.isEmpty()) {
                domain = domainHistory.pop();
            }
        }
    }

    static abstract class Constraint {
        public abstract List<Integer> GetDomainForVariable(Variable var, List<Integer> domain);
        public abstract boolean containsVariable(Variable var);
    }

    static class NotEqConstraint extends Constraint {
        private final Variable x1;
        private final Variable x2;
        private final int c;

        /**
         * Constructs a NotEqConstraint:
         *    x1 != x2 + c
         * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
         *     However, you are allowed to change its behavior in any way you want.
         *
         * @param x1 The first variable.
         * @param x2 The second variable.
         * @param c An integer constant.
         */
        public NotEqConstraint(Variable x1, Variable x2, int c) {
            // Variable initialization
            this.x1 = x1;
            this.x2 = x2;
            this.c = c;
        }

        public List<Integer> GetDomainForVariable(Variable var, List<Integer> domain)
        {
            if (x1.equals(var)) {
                if (x2.isAssigned()) {
                    domain.removeIf(value -> value == x2.AssignedValue + c);
                } else if (x2.domain.size() == 1){
                    domain.removeIf(value -> value == x2.Min() + c);
                }
            } else if (x2.equals(var)) {
                if (x1.isAssigned()) {
                    domain.removeIf(value -> value == x1.AssignedValue - c);
                } else if (x1.domain.size() == 1){
                    domain.removeIf(value -> value == x1.Min() + c);
                }
            }
            return domain;
        }

        public boolean containsVariable(Variable var){
            return x1.equals(var) || x2.equals(var);
        }
    }

    static class AllDiffConstraint extends Constraint {
        private final HashSet<Variable> xs;

        /**
         * Constructs an AllDiffConstraint:
         *    AllDifferent(x1, ..., xn)
         * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
         *     However, you are allowed to change its behavior in any way you want.
         *
         * @param xs An array of a variables that should be different.
         */
        public AllDiffConstraint(Variable[] xs) {
            // Variable initialization
            this.xs = new HashSet<>(List.of(xs));
        }

        public List<Integer> GetDomainForVariable(Variable var, List<Integer> domain)
        {
            for (Variable x : xs) {
                if(x.equals(var))
                    continue;
                else if (x.isAssigned()) {
                    domain.remove(x.AssignedValue);
                } else if (x.domain.size() == 1) {
                    domain.remove(x.Min());
                }

                if(domain.isEmpty())
                    return domain;
            }

            return domain;
        }

        public boolean containsVariable(Variable var){
            return xs.contains(var);
        }
    }

    static class IneqConstraint extends Constraint {
        private final List<Variable> xs;
        private final int[] ws;
        private final int c;

        /**
         * Constructs an IneqConstraint:
         *    w1 * x1 + ... + wn * xn >= c
         * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
         *     However, you are allowed to change its behavior in any way you want.
         *
         * @param xs An array of all variables involved.
         * @param ws An array of respective integer weights.
         * @param c An integer constant.
         */
        public IneqConstraint(Variable[] xs, int[] ws, int c) {
            // Variable initialization
            this.xs = new ArrayList<>(List.of(xs));
            this.ws = ws;
            this.c = c;
        }

        public List<Integer> GetDomainForVariable(Variable var, List<Integer> possibleOptions) {
            boolean isMinimize = ws[xs.stream().toList().indexOf(var)] < 0;

            if (isMinimize) {
                Collections.sort(possibleOptions);
            } else {
                possibleOptions.sort(Collections.reverseOrder());
            }

            if(calculateSum(possibleOptions.get(0), var) < c)
                return new ArrayList<>();

            for (int i = possibleOptions.size() - 1 ; i >= 0; i--) {
                int testedOption = possibleOptions.get(i),
                total = calculateSum(testedOption, var);

                if (total >= c) {
                    possibleOptions.subList(i + 1, possibleOptions.size()).clear();

                    return possibleOptions;
                }
            }

            return new ArrayList<>();
        }

        private Integer calculateSum(Integer testedOption, Variable var){
            int total = 0;
            for (int j = 0; j < xs.size(); j++) {
                var x = xs.get(j);
                if (var.equals(x)) {
                    total += testedOption * ws[j];
                } else if (x.isAssigned()) {
                    total += x.AssignedValue * ws[j];
                } else
                {
                        if (ws[j] >= 0)
                            total += x.Max() * ws[j];
                        else
                            total += x.Min() * ws[j];
                    }
                }


            return total;
        }

        public boolean containsVariable(Variable var){
            return xs.contains(var);
        }
    }

    private final List<Constraint> constraints;
    private final List<Variable> variables;
    private final List<int[]> foundSolutions;

    /**
     * Constructs a Solver using a list of variables and constraints.
     * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
     *     However, you are allowed to change its behavior in any way you want.
     *
     */
    public Solver(Variable[] variables, Constraint[] constraints) {
        // Initialize variables
        this.variables = new ArrayList<>(List.of(variables));
        this.constraints = new ArrayList<>(List.of(constraints));
        this.foundSolutions = new LinkedList<>();
    }

    /**
     * Attempts to find one solution that satisfies the constraints.
     *     Should terminate immediately when a solution is found.
     *     Should return null if no solution exists.
     * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
     *     However, you are allowed to change its behavior in any way you want.
     *
     * @return An integer-array values to assign to the variables,
     *             in the order they are provided.
     */
    public int[] findOneSolution() {
        // Find a solution
        foundSolutions.clear();
        solve(false);

        // Return the found solution (or null if it doesn't exist)
        if (foundSolutions.isEmpty())
            return null;
        return foundSolutions.get(0);
    }

    /**
     * Attempts to find all solutions that satisfy the constraints.
     * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
     *     However, you are allowed to change its behavior in any way you want.
     *
     * @return A list of integer-arrays, each representing a different
    solution to the problem (same format as findOneSolution).
     */
    public List<int[]> findAllSolutions() {
        // Find a solution
        foundSolutions.clear();
        solve(true);

        // Return all found solutions
        return foundSolutions;
    }

    /**
     * Applies search and inference to find value assignments to each variable,
     *    such that all constraints are satisfied. Any found solution is added
     *    to the list `foundSolutions`.
     * You are allowed to change or even remove this method.
     *
     * @param findAll True if all solutions must be found, false if only
     *                    only one needs to be found.
     */
    private void solve(boolean findAll) {
        Propagate(findAll);
    }

    private boolean Propagate(boolean findAll) {
        Variable variable = selectSmallestVariable();
        if (variable == null) {
            AddSolution();
            return !findAll;
        }

        HashSet<Variable> connectedVariables = getAllConnectedVariables(variable);
        for (int value : orderValues(variable)) {
            variable.Assign(value);

            if (prune(connectedVariables)) {
                if (Propagate(findAll) && !findAll) {
                    return true;
                }
            }

            variable.Unassign();
            RestoreDomains(connectedVariables);
        }
        return false;
    }

    private List<Integer> orderValues(Variable var) {
        List<Integer> values = new ArrayList<>(var.domain);
        values.sort(Collections.reverseOrder());
        return values;
    }

    private void RestoreDomains(HashSet<Variable> connectedVariables) {
        for(Variable var: connectedVariables){
            var.restoreDomain();
        }
    }

    private void AddSolution(){
        int[] solution = new int[variables.size()];
        for (int i = 0; i < variables.size(); i++) {
            solution[i] = variables.get(i).AssignedValue;
        }
        foundSolutions.add(solution);
    }

    private List<Integer> GetDomainForVariableOverConstraints(Variable var){
        List<Integer> domain = new ArrayList<>(var.domain);
        for(var constraint: constraints){
            if(constraint.containsVariable(var)) {
                domain = constraint.GetDomainForVariable(var, domain);
                if (domain.isEmpty())
                    return domain;
            }
        }
        return domain;
    }

    private Variable selectSmallestVariable() {
        return variables.stream()
            .filter(v -> !v.isAssigned())
            .min(Comparator
                .comparingInt((Variable v) -> v.domain.size()))
            .orElse(null);
    }


    private boolean prune(HashSet<Variable> allConnectedVariables) {
        for(Variable var: allConnectedVariables){
            if (!var.isAssigned()) {
                var.saveDomain();

                List<Integer> newDomain = GetDomainForVariableOverConstraints(var);

                if (newDomain.isEmpty()) {
                    return false;
                }

                var.domain.retainAll(newDomain);
            }
        }
        return true;
    }

    private HashSet<Variable> getAllConnectedVariables(Variable var){
        if(var.connectedVariables != null)
            return var.connectedVariables;
        HashSet<Variable> allConnectedVariables = new HashSet<>();
        for (Constraint constraint : constraints) {
            if (constraint.containsVariable(var)) {
                getConnectedVariables(constraint, allConnectedVariables);
            }
        }
        var.connectedVariables = allConnectedVariables;
        return allConnectedVariables;
    }

    private void getConnectedVariables(Constraint constraint, HashSet<Variable> set) {
        if (constraint instanceof NotEqConstraint notEq) {
            set.add(notEq.x2);
            set.add(notEq.x1);
        } else if (constraint instanceof AllDiffConstraint allDif) {
            set.addAll(allDif.xs);
        } else if (constraint instanceof IneqConstraint ineq) {
            set.addAll(ineq.xs);
        }
    }
}