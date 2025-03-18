import java.util.*;

class Solver {
    static class Variable {
        public SortedSet<Integer> domain;
        public Integer AssignedValue;
        private Stack<SortedSet<Integer>> domainHistory;

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
        private Variable x1;
        private Variable x2;
        private int c;

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
                }
            } else if (x2.equals(var)) {
                if (x1.isAssigned()) {
                    domain.removeIf(value -> value == x1.AssignedValue - c);
                }
            }
            return domain;
        }

        public boolean containsVariable(Variable var){
            return x1.equals(var) || x2.equals(var);
        }
    }

    static class AllDiffConstraint extends Constraint {
        private HashSet<Variable> xs;

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
            Set<Integer> toRemove = new HashSet<>();

            for (Variable x : xs) {
                if(x.equals(var))
                    continue;
                else if (x.isAssigned()) {
                    toRemove.add(x.AssignedValue);
                } else if (x.domain.size() == 1) {
                    toRemove.add(x.Min());
                }

                if(domain.isEmpty())
                    return domain;
            }

            domain.removeAll(toRemove);

            return domain;
        }

        public boolean containsVariable(Variable var){
            return xs.contains(var);
        }
    }

    static class IneqConstraint extends Constraint {
        private Variable[] xs;
        private int[] ws;
        private int c;

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
            this.xs = xs;
            this.ws = ws;
            this.c = c;
        }

        public List<Integer> GetDomainForVariable(Variable var, List<Integer> possibleOptions) {
            boolean isMinimize = ws[Arrays.stream(xs).toList().indexOf(var)] < 0;

            if (isMinimize) {
                Collections.sort(possibleOptions);
            } else {
                possibleOptions.sort(Collections.reverseOrder());
            }

            for (int i = possibleOptions.size() - 1 ; i >= 0; i--) {
                int total = 0, testedOption = possibleOptions.get(i);

                for (int j = 0; j < xs.length; j++) {
                    if (var.equals(xs[j])) {
                        total += testedOption * ws[j];
                    } else if (xs[j].isAssigned()) {
                        total += xs[j].AssignedValue * ws[j];
                    } else {
                        if (ws[j] >= 0)
                            total += xs[j].Max() * ws[j];
                        else
                            total += xs[j].Min() * ws[j];
                    }
                }

                if (total >= c) {
                    possibleOptions.subList(i + 1, possibleOptions.size()).clear();

                    return possibleOptions;
                }
            }

            return new ArrayList<>();
        }


        public boolean containsVariable(Variable var){
            return Arrays.asList(xs).contains(var);
        }
    }

    private Constraint[] constraints;
    private Variable[] variables;
    private List<int[]> foundSolutions;

    /**
     * Constructs a Solver using a list of variables and constraints.
     * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
     *     However, you are allowed to change its behavior in any way you want.
     *
     * @return An integer-array values to assign to the variables,
     *             in the order they are provided.
     */
    public Solver(Variable[] variables, Constraint[] constraints) {
        // Initialize variables
        this.variables = variables;
        this.constraints = constraints;
        this.foundSolutions = new LinkedList<>();

        // TODO: Add any more logic you want here...
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
        Propogate(findAll);
    }

    private boolean Propogate(boolean findAll) {
        Variable variable = selectSmallestVariable();
        if (variable == null) {
            AddSolution();
            return !findAll;
        }

        HashSet<Variable> connectedVariables = getAllConnectedVariables(variable);
        for (int value : variable.domain) {
            variable.Assign(value);

            if (prune(connectedVariables)) {
                if (Propogate(findAll) && !findAll) {
                    return true;
                }
            }

            variable.Unassign();
            RestoreDomains(connectedVariables);
        }
        return false;
    }

    private void RestoreDomains(HashSet<Variable> connectedVariables) {
        for(Variable var: connectedVariables){
            var.restoreDomain();
        }
    }

    private void AddSolution(){
        int[] solution = new int[variables.length];
        for (int i = 0; i < variables.length; i++) {
            solution[i] = variables[i].AssignedValue;
        }
        foundSolutions.add(solution);
    }

    private List<Integer> GetDomainForVariableOverConstraints(Variable var){
        List<Integer> domain = new ArrayList<>(var.domain);
        for(var constraint: constraints){
            if(constraint.containsVariable(var))
                domain = constraint.GetDomainForVariable(var, domain);
        }
        return domain;
    }

    private Variable selectSmallestVariable() {
        return Arrays.stream(variables)
            .filter(v -> !v.isAssigned())
            .min(Comparator.comparingInt((Variable v) -> v.domain.size()))
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

                var.domain.clear();
                var.domain.addAll(newDomain);
            }
        }
        return true;
    }

    private HashSet<Variable> getAllConnectedVariables(Variable var){
        HashSet<Variable> allConnectedVariables = new HashSet<>();
        for (Constraint constraint : constraints) {
            if (constraint.containsVariable(var)) {
                getConnectedVariables(constraint, allConnectedVariables);
            }
        }
        return allConnectedVariables;
    }

    private void getConnectedVariables(Constraint constraint, HashSet<Variable> set) {
        if (constraint instanceof NotEqConstraint notEq) {
            set.add(notEq.x2);
            set.add(notEq.x1);
        } else if (constraint instanceof AllDiffConstraint allDif) {
            set.addAll(allDif.xs);
        } else if (constraint instanceof IneqConstraint ineq) {
            set.addAll(List.of(ineq.xs));
        }
    }
}