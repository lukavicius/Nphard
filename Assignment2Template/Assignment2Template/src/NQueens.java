import java.util.*;

// Do not copy the entire class to WebLab, only the contents of the "solveProblem"-method
// On WebLab, your class should always just be called "Solution"
class NQueens {
    /**
     * Returns the number of solutions to the N-Queens problem.
     *
     * @param n The number of queens and the size of the board.
     * @return The number of valid ways to arrange the queens.
     */
    public static int solveProblem(int n) {
        Solver.Variable[] variables = new Solver.Variable[n];
        // TODO: add your variables

        for(int i = 0 ; i < n ; i ++){
            variables[i] = new Solver.Variable(createList(n));
        }

        List<Solver.Constraint> constraints = new ArrayList<>();
        constraints.add(new Solver.AllDiffConstraint(variables));

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                constraints.add(new Solver.NotEqConstraint(
                    variables[i],
                    variables[j],
                    i - j
                ));

                constraints.add(new Solver.NotEqConstraint(
                    variables[i],
                    variables[j],
                    j - i
                ));
            }
        }

        // Use solver
        Solver solver = new Solver(
            variables,
            constraints.toArray(new Solver.Constraint[0])
        );
        List<int[]> result = solver.findAllSolutions();

        // TODO: construct solution using result
        return result.size();
    }

    public static ArrayList<Integer> createList(int end) {
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < end; i++){
            list.add(i);
        }
        return list;
    }
}
