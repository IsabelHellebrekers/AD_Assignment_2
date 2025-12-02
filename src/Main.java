package src;

/**
 * Main class to run a greedy constructive heuristic, a VND algorithm and 
 * a GRASP heuristic on multiple CVRP instances.
 */
public class Main {
    public static void main(String[] args) throws Exception{
        double alpha = 0.3;
        long timeLimitMillis = 10 * 60 * 1000L;
        GRASP grasp = new GRASP(alpha);

        for (int i = 1; i <= 1; i++) {
            CVRPInstance inst = InstanceReader.read("instances/instance" + i + ".txt");

            CVRPSolution greedySol = new SavingsHeuristic().construct(inst);
            System.out.println("Greedy solution instance "+ i + ": " + greedySol.getTotalDistance(inst));

            CVRPSolution VNDSol = new VND().solve(greedySol, inst);
            System.out.println("VND solution instance "+ i + ": " + VNDSol.getTotalDistance(inst));
            
            CVRPSolution graspSol = grasp.solve(inst, timeLimitMillis);
            System.out.println("GRASP solution instance "+ i + ": " + graspSol.getTotalDistance(inst));

            String out = "solutions/solution_" + i + ".txt";
            SolutionWriter.writeSolution(out, graspSol);

            System.out.println("Generated " + out);
        }
    }
}