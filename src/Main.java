package src;

public class Main {
    public static void main(String[] args) {
        try {
            CVRPInstance inst = InstanceReader.read("instances/instance5.txt");

            SavingsHeuristic sh = new SavingsHeuristic();
            CVRPSolution sol = sh.construct(inst);

            System.out.println("Aantal routes: " + sol.getNumberOfRoutes());
            System.out.println("Totale cost : " + sol.getTotalDistance(inst));
            System.out.println("Feasible    : " + sol.isFeasible(inst));

            int rId = 1;
            for (Route r : sol.getRoutes()) {
                System.out.print("Route " + rId++ + ": 1 ");
                for (int c : r.getCustomers()) {
                    System.out.print("-> " + c + " ");
                }
                System.out.println("-> 1");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
