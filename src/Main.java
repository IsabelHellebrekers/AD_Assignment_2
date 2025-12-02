package src;

public class Main {
    public static void main(String[] args) throws Exception{
        // CVRPInstance inst = InstanceReader.read("instances/instance5.txt");
        
        double alpha = 0.3;
        long timeLimitMillis = 10 * 60 * 1000L;

        GRASP grasp = new GRASP(alpha);
        // CVRPSolution best = grasp.solve(inst, timeLimitMillis);

        // System.out.println("Best objective: "+ best.getTotalDistance(inst));

        for (int i = 1; i <= 1; i++) {
            CVRPInstance inst = InstanceReader.read("instances/instance" + i + ".txt");
            CVRPSolution best = grasp.solve(inst, timeLimitMillis);

            String out = "solution_" + i + ".txt";
            SolutionWriter.writeSolution(out, best);

            System.out.println("Generated " + out);
        }
    }
}