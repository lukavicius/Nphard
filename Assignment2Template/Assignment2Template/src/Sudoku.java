import java.util.*;

// Do not copy the entire class to WebLab, only the contents of the "solveProblem"-method
// On WebLab, your class should always just be called "Solution"
class Sudoku {
    /**
     * Returns the filled in sudoku grid.
     *
     * @param sudoku The partially filled in sudoku grid. Unfilled positions are marked with -1. Always either of size 9x9, 16x16 or 25x25.
     * @return The fully filled in sudoku grid.
     */
    public static int[][] solveProblem(int[][] sudoku) {

        int n = sudoku.length;
        int sqrt_n = 1;
        while (sqrt_n * sqrt_n < n)
            sqrt_n++;

        Solver.Variable[] variables = new Solver.Variable[sudoku.length * sudoku.length];
        // TODO: add your variables

        for(int i = 0; i < sudoku.length * sudoku.length; i++){
            int num = sudoku[i/sudoku.length][i%sudoku.length];
            if(num == -1)
                variables[i] = new Solver.Variable(createList(sudoku.length));
            else variables[i] = new Solver.Variable(new ArrayList<>(List.of(num)));
        }

        List<Solver.Constraint> constraints = new ArrayList<>();
        // TODO: add your constraints
        for(int i = 0; i < sudoku.length; i ++){
            Solver.Variable[] toConstrainRow = new Solver.Variable[sudoku.length];
            Solver.Variable[] toConstrainColumn = new Solver.Variable[sudoku.length];
            for (int j = 0; j < sudoku.length; j++){
                toConstrainRow[j] = variables[i * sudoku.length + j];
                toConstrainColumn[j] = variables[j * sudoku.length + i];
            }

            constraints.add(new Solver.AllDiffConstraint(toConstrainRow));
            constraints.add(new Solver.AllDiffConstraint(toConstrainColumn));
        }

        for(int i = 0; i < sqrt_n; i++){
            for(int j = 0; j < sqrt_n; j++){
                Solver.Variable[] toConstrainBlock = new Solver.Variable[sudoku.length];
                for(int k = 0; k < sqrt_n; k++){
                    for(int l = 0; l < sqrt_n; l++){
                        toConstrainBlock[k * sqrt_n + l] =
                            variables[i * sqrt_n * sudoku.length + j * sqrt_n + k * sudoku.length + l];
                    }
                }

                constraints.add(new Solver.AllDiffConstraint(toConstrainBlock));
            }
        }

        // Use solver
        Solver solver = new Solver(
            variables,
            constraints.toArray(new Solver.Constraint[0])
        );
        int[] result = solver.findOneSolution();
        int[][] solution = new int[sudoku.length][sudoku.length];
        for(int i = 0; i < sudoku.length * sudoku.length; i++){
            solution[i/sudoku.length][i%sudoku.length] = result[i];
        }

        // TODO: construct solution using result
        return solution;
    }

    public static ArrayList<Integer> createList(int end) {
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 1; i <= end; i++){
            list.add(i);
        }
        return list;
    }
}
