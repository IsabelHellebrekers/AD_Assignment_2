package src;

public class Main {
    public static void main(String[] args) throws Exception{
        double alpha = 0.3;
        long timeLimitMillis = 10 * 60 * 1000L;
        GRASP grasp = new GRASP(alpha);

        for (int i = 2; i <= 5; i++) {
            CVRPInstance inst = InstanceReader.read("instances/instance" + i + ".txt");
            CVRPSolution best = grasp.solve(inst, timeLimitMillis);

            String out = "solutions/solution_" + i + ".txt";
            SolutionWriter.writeSolution(out, best);

            System.out.println("Generated " + out);
        }
    }
}