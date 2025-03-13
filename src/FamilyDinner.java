import java.util.*;

// Do not copy the entire class to WebLab, only the contents of the "solveProblem"-method
// On WebLab, your class should always just be called "Solution"
class FamilyDinner {
    /**
     * Attempts to find a solution to the Family Dinner problem.
     *
     * @param names A complete list of names of every person attending the dinner.
     * @param hates A list of pairs of people who should not be seated next to each other.
     * @return An array indicating who sits where around the table. null if there is no way to allocate everyone without breaking a constraint.
     */
    public static String[] solveProblem(String[] names, List<String[]> hates) {
        int n = names.length;

        Solver.Variable[] variables = new Solver.Variable[names.length];
        // TODO: add your variables

        for(int i = 0; i < names.length; i ++){
            variables[i] = new Solver.Variable(createList(names.length));
        }
        List<Solver.Constraint> constraints = new ArrayList<>();
        // TODO: add your constraints

        constraints.add(new Solver.AllDiffConstraint(variables));

        for (int i = 0 ; i < hates.size(); i ++) {
            String[] hatePair = hates.get(i);

            int firstPerson = -1, secondPerson = -1;

            for (int j = 0; j < names.length; j++) {
                if (names[j].equals(hatePair[0]))
                    firstPerson = j;
                if (names[j].equals(hatePair[1]))
                    secondPerson = j;
            }

            constraints.add(new Solver.NotEqConstraint(variables[firstPerson], variables[secondPerson], 1));
            constraints.add(new Solver.NotEqConstraint(variables[firstPerson], variables[secondPerson], -1));
            constraints.add(new Solver.NotEqConstraint(variables[firstPerson], variables[secondPerson], names.length - 1));
            constraints.add(new Solver.NotEqConstraint(variables[firstPerson], variables[secondPerson], - names.length + 1));
        }

        // Use solver
        Solver solver = new Solver(
            variables,
            constraints.toArray(new Solver.Constraint[0])
        );
        int[] result = solver.findOneSolution();
        if (result == null) {
            return null;
        }

        String[] solution = new String[n];
        for(int i = 0; i < names.length ; i ++){
            solution[result[i]] = names[i];
        }
        return solution;
    }

    public static ArrayList<Integer> createList(int end) {
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < end; i++){
            list.add(i);
        }
        return list;
    }
}
