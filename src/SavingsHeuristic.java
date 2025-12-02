package src;
import java.util.*;

/**
 * Greedy Savings Heuristic for constructing an initial CVRP solution.
 */
public class SavingsHeuristic {
    /**
     * Helper class for savings between two customers.
     */
    private static class Saving {
        int i;
        int j;
        int saving;

        Saving(int i, int j, int saving) {
            this.i = i;
            this.j = j;
            this.saving = saving;
        }
    }

    /**
     * Constructs a CVRPSolution using the Savings Heuristic
     * @param instance CVRP instance
     * @return Constructed CVRPSolution
     */
    public CVRPSolution construct(CVRPInstance instance) {
        int n = instance.getNodes();
        int capacity = instance.getCapacity();

        CVRPSolution sol = new CVRPSolution();

        // To keep track of which route each customer is in
        Route[] routeOf = new Route[n + 1];

        // Initialize each customer in its own route (1 -> customer -> 1)
        for (int i = 2; i <= n; i++) {
            Route r = new Route();
            r.addCustomerToEnd(i, instance);
            sol.addRoute(r);
            routeOf[i] = r;
        }

        // Compute all savings
        List<Saving> savings = new ArrayList<>();
        for (int i = 2; i <= n; i++) {
            for (int j = i + 1; j <= n; j++) {
                int s = instance.getDistance(1, i)
                        + instance.getDistance(1, j)
                        - instance.getDistance(i, j);
                savings.add(new Saving(i, j, s));
            }
        }

        // Sort savings in descending order
        Collections.sort(savings, new Comparator<Saving>() {
            @Override
            public int compare(Saving a, Saving b) {
                return Integer.compare(b.saving, a.saving);
            }
        });

        for (Saving s : savings) {
            int i = s.i;
            int j = s.j;

            Route ri = routeOf[i];
            Route rj = routeOf[j];

            if (ri == null || rj == null) continue;
            if (ri == rj) continue;

            // customer i and j has to be at the end of their current route
            boolean iAtEnd = (ri.getFirstCustomer() == i || ri.getLastCustomer() == i);
            boolean jAtEnd = (rj.getFirstCustomer() == j || rj.getLastCustomer() == j);
            if (!iAtEnd || !jAtEnd) continue;

            int newDemand = ri.getDemand() + rj.getDemand();
            if (newDemand > capacity) continue;

            // Ensure that customer i is at the end of ri
            if (ri.getFirstCustomer() == i && ri.getLastCustomer() != i) {
                ri.reverse();
            }
            // and customer j is at the start of rj
            if (rj.getLastCustomer() == j && rj.getFirstCustomer() != j) {
                rj.reverse();
            }

            if (ri.getLastCustomer() != i || rj.getFirstCustomer() != j) {
                continue;
            }

            // Merge routes i and j 
            for (int c : rj.getCustomers()) {
                ri.addCustomerToEnd(c, instance);
                routeOf[c] = ri;
            }

            // Remove route j
            sol.getRoutes().remove(rj);
        }
        return sol;
    }
}
