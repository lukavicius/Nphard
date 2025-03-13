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

        public Integer Max(){
            return Collections.max(domain);
        }

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

        public void remove(int x){
            domain.remove(x);
        }

        public void removeBelow(int y){
            domain.removeIf(x -> x < y);
        }

        public void removeAbove(int y){
            domain.removeIf(x -> x > y);
        }
        // TODO: Add any methods you want here...
    }

    static abstract class Constraint {
        public abstract boolean check();
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

        public boolean check(){
            Integer assignedSize = (int) Arrays.stream(xs).filter(Variable::isAssigned).map(x-> x.AssignedValue).count();
            Integer distinctSize = (int) Arrays.stream(xs).filter(Variable::isAssigned).map(x-> x.AssignedValue).distinct().count();
            return assignedSize == distinctSize;
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
                if (!xs[i].isAssigned()) return true;
                total += xs[i].AssignedValue * ws[i];
            }
            return total >= c;
        }
        // TODO: Add any methods you want here...
    }

    private Constraint[] constraints;
    private Variable[] variables;
    private List<int[]> foundSolutions;
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
        Propogate(0, findAll);
    }

    private boolean Propogate(int level, boolean findAll) {
        if (level == variables.length) {
            int[] solution = new int[variables.length];
            for (int i = 0; i < variables.length; i++) {
                solution[i] = variables[i].AssignedValue;
            }
            foundSolutions.add(solution);
            return !findAll;
        }

        Variable var = variables[level];
        List<Integer> variableOptions = new ArrayList<>(var.domain);

        for (int value : variableOptions) {
            var.Assign(value);
            if (CheckConstraints()) {
                if (Propogate(level + 1, findAll) && !findAll) {
                    return true;
                }
            }
            var.Unassign();
        }
        return false;
    }

    private boolean CheckConstraints() {
        for (Constraint c : constraints) {
            if (!c.check()) return false;
        }
        return true;
    }

    // You are free to add any helper methods you might want to use within
    //     the solver. Note, however, that you would not be allowed to call
    //     them directly from within the `solveProblem`-method, since you
    //     must copy your `solveProblem`-code from Part 1, which wouldn't
    //     have these helper methods defined.

}