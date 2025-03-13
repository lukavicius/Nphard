import java.util.*;

// Do not copy the entire class to WebLab, only the contents of the "solveProblem"-method
// On WebLab, your class should always just be called "Solution"
class Production {
    /**
     * Attempts to find a solution to the Production problem.
     *
     * @param C The number of units available for each type of material.
     * @param c A 2D-array describing how many units of a type of material are needed to produce on instance of a particular product-type.
     * @param P The maximum number of instances that can be made of a product-type.
     * @param r How much revenue one instance of a product-type will yield.
     * @param R The desired minimum revenue.
     * @return An array describing how many instances of each product-type should be produced to obtained enough revenue. null if this is not possible.
     */
    public static int[] solveProblem(int[] C, int[][] c, int[] P, int[] r, int R) {
        Solver.Variable[] variables = new Solver.Variable[P.length];
        for(int i = 0 ; i < P.length; i++){
            variables[i] = new Solver.Variable(createList(P[i]));
        }

        List<Solver.Constraint> constraints = new ArrayList<>();
        // TODO: add your constraints
        constraints.add(new Solver.IneqConstraint(variables, r, R));

        for (int i = 0; i < C.length; i++) {
            int[] materialsUsed = new int[P.length];
            for (int j = 0; j < P.length; j++) {
                materialsUsed[j] = c[i][j];
            }
            constraints.add(new Solver.IneqConstraint(variables, minusArray(materialsUsed), -C[i]));
        }

        // Use solver
        Solver solver = new Solver(
            variables,
            constraints.toArray(new Solver.Constraint[0])
        );

        int[] result = solver.findOneSolution();

        // TODO: construct solution using result
        return result;
    }

    public static int[] minusArray(int[] array){
        for(int i = 0 ; i < array.length; i ++){
            array[i] = - array[i];
        }
        return array;
    }

    public static ArrayList<Integer> createList(int end) {
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i <= end; i++){
            list.add(i);
        }
        return list;
    }
}
