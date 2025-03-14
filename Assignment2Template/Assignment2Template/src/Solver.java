import java.util.*;

// Copy the below class to each of the Part 2 WebLab exercises
//    when submitting your solution.
// Make sure you copy the EXACT same version of your Solver in each
//    of the five exercises before the deadline, you'll be
//    automatically flagged for review if you don't!

// Write your solver below here.
// A template is already provided. You are allowed to deviate from this
//     template, as long as all classes and methods mentioned in the
//     description exist.

class Solver {
    static class Variable {
        public List<Integer> domain;
        public Integer AssignedValue;
        // TODO: Add any fields you want here...

        /**
         * Constructs a Variable with a specified domain.
         * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
         *     However, you are allowed to change its behavior in any way you want.
         *
         * @param domain A list of integers, representing the domain of the variable.
         */
        public Variable(List<Integer> domain) {
            // Variable initialization
            this.domain = new ArrayList<>(domain);

            // TODO: Add any more logic you want here...
        }

        public Integer Min(){
            return Collections.min(domain);
        }

        public Integer Max(){ return Collections.max(domain); }

        public boolean contains(int x){
            return domain.contains(x);
        }

        public void Assign(int x){
            AssignedValue = x;
        }

        public void Unassign(){
            AssignedValue = null;
        }

        public boolean isAssigned() {
            return AssignedValue != null;
        }

        // TODO: Add any methods you want here...
    }

    static abstract class Constraint {
        public abstract boolean check();
        public abstract List<Integer> GetDomainForVariable(Variable var, List<Integer> domain);
        public abstract boolean containsVariable(Variable var);
    }

    static class NotEqConstraint extends Constraint {
        private Variable x1;
        private Variable x2;
        private int c;
        // TODO: Add any fields you want here...

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

            // TODO: Add any more logic you want here...
        }

        public boolean check(){
            if(!x1.isAssigned() || !x2.isAssigned())
                return true;
            return x1.AssignedValue != x2.AssignedValue + c;
        }
        // TODO: Add any methods you want here...
        public List<Integer> GetDomainForVariable(Variable var, List<Integer> domain)
        {
            if(x1.equals(var))
            {
                if(x2.isAssigned()){
                    if(domain.contains(x2.AssignedValue + c))
                        domain.removeAll(Arrays.asList(x2.AssignedValue + c));
                } else if(x2.domain.size() == 1){
                    if(domain.contains(x2.domain.get(0) + c))
                        domain.removeAll(Arrays.asList(x2.domain.get(0) + c));
                }
            }
            else
            {
                if(x1.isAssigned()){
                    if(domain.contains(x1.AssignedValue - c))
                        domain.removeAll(Arrays.asList(x1.AssignedValue - c));
                } else if(x1.domain.size() == 1){
                    if(domain.contains(x1.domain.get(0) - c))
                        domain.removeAll(Arrays.asList(x1.domain.get(0) - c));
                }
            }

            return domain;
        }

        public boolean containsVariable(Variable var){
            return x1.equals(var) || x2.equals(var);
        }
    }

    static class AllDiffConstraint extends Constraint {
        private Variable[] xs;
        // TODO: Add any fields you want here...

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
            this.xs = xs;

            // TODO: Add any more logic you want here...
        }

        public boolean check() {
            Set<Integer> assigned = new HashSet<>();
            for (Variable var : xs) {
                if (var.isAssigned()) {
                    if (!assigned.add(var.AssignedValue)) return false;
                }
            }
            return true;
        }

        public List<Integer> GetDomainForVariable(Variable var, List<Integer> domain)
        {
            Set<Integer> toRemove = new HashSet<>();

            for (Variable x : xs) {
                if(x.equals(var))
                    continue;
                if (x.isAssigned()) {
                    toRemove.add(x.AssignedValue);
                } else if (x.domain.size() == 1) {
                    toRemove.add(x.domain.get(0));
                }
            }

            domain.removeAll(toRemove);

            return domain;
        }

        public boolean containsVariable(Variable var){
            return Arrays.asList(xs).contains(var);
        }
    }

    static class IneqConstraint extends Constraint {
        private Variable[] xs;
        private int[] ws;
        private int c;
        // TODO: Add any fields you want here...

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

            // TODO: Add any more logic you want here...
        }

        public boolean check(){
            int total = 0;
            for(int i = 0; i < xs.length; i++){
                if (xs[i].isAssigned())
                    total += xs[i].AssignedValue * ws[i];
                else
                    return true;
            }
            return total >= c;
        }

        public List<Integer> GetDomainForVariable(Variable var, List<Integer> domain) {
            List<Integer> possibleOptions = new ArrayList<>(domain.stream().sorted().toList());
            boolean isMinimize = ws[Arrays.stream(xs).toList().indexOf(var)] < 0;
            if(isMinimize){
                for (int i = 0; i < possibleOptions.size(); i ++) {
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

                    if (total < c) {
                        var filtered =  new ArrayList<>(possibleOptions.stream().filter(x -> x < testedOption).toList());
                        return filtered;
                    }
                }
            }
            else {
                for (int i = possibleOptions.size() - 1; i >= 0; i--) {
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

                    if (total < c) {
                        var filtered = new ArrayList<>(possibleOptions.stream().filter(x -> x > testedOption).toList());
                        return filtered;
                    }
                }
            }

            return possibleOptions;
        }

        public boolean containsVariable(Variable var){
            return Arrays.asList(xs).contains(var);
        }
    }

    private Constraint[] constraints;
    private Variable[] variables;
    private List<int[]> foundSolutions;
    private int solutionsExplored = 0;
    // TODO: Add any fields you want here...

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
        System.out.println(solutionsExplored);
    }

    private boolean Propogate(boolean findAll) {
        var variable = selectSmallestVariable();
        if (variable == null) {
            int[] solution = new int[variables.length];
            for (int i = 0; i < variables.length; i++) {
                solution[i] = variables[i].AssignedValue;
            }
            foundSolutions.add(solution);
            return !findAll;
        }

        List<Integer> domain = new ArrayList<>(variable.domain);
        for(var constraint: constraints){
            if(constraint.containsVariable(variable))
                domain = constraint.GetDomainForVariable(variable, domain);
        }

        for (int value : domain) {
            variable.Assign(value);
            if (CheckConstraintsFor(variable)) {
                solutionsExplored++;
                if (Propogate(findAll) && !findAll) {
                    return true;
                }
            }
            variable.Unassign();
        }
        return false;
    }

    private boolean CheckConstraintsFor(Variable var) {
        for (Constraint c : constraints) {
            if (c.containsVariable(var) && !c.check()) return false;
        }
        return true;
    }

    private boolean CheckConstraints() {
        for (Constraint c : constraints) {
            if (!c.check()) return false;
        }
        return true;
    }

    private Variable selectSmallestVariable() {
        return Arrays.stream(variables)
            .filter(v -> !v.isAssigned())
            .min(Comparator.comparingInt(v -> v.domain.size()))
            .orElse(null);
    }


    // You are free to add any helper methods you might want to use within
    //     the solver. Note, however, that you would not be allowed to call
    //     them directly from within the `solveProblem`-method, since you
    //     must copy your `solveProblem`-code from Part 1, which wouldn't
    //     have these helper methods defined.

}