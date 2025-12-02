package src;

import java.util.*;

/**
 * This class implements a GRASP heuristic for the CVRP.
 */
public class GRASP {
    private final double alpha;
    private final VND vnd;
    private final Random rng = new Random(42);

    public GRASP(double alpha) {
        this.alpha = alpha;
        this.vnd = new VND();
    }

    /**
     * Solves the given CVRP instance using GRASP within the specified time limit.
     * @param instance CVRP instance
     * @param timeLimitMillis time limit in milliseconds
     * @return
     */
    public CVRPSolution solve(CVRPInstance instance, long timeLimitMillis) {
        long start = System.currentTimeMillis();

        // initialization
        CVRPSolution bestSol = null;
        int bestDist = Integer.MAX_VALUE;

        while (System.currentTimeMillis() - start < timeLimitMillis) {
            // construct randomized greedy solution
            CVRPSolution sol = constructInitalSol(instance);

            // improve solution using VND
            sol = vnd.solve(sol, instance);

            // update best solution found so far
            int cost = computeSolutionDistance(sol, instance);
            if (cost < bestDist) {
                bestDist = cost;
                bestSol = sol;
                System.out.println("--> NEW BEST SOLUTION: " + bestDist);
            }
        }
        return bestSol;
    }

    /**
     * Helper class to represent savings between merging two routes.
     */
    private static class Saving {
        int i;
        int j;
        int value;

        Saving(int i, int j, int value)  {
            this.i = i;
            this.j = j;
            this.value = value;
        }
    }

    /**
     * Constructs an initial solution using a randomized savings heuristic.
     * @param instance CVRP instance
     * @return initial solution for GRASP
     */
    private CVRPSolution constructInitalSol(CVRPInstance instance) {
        int n = instance.getNodes();
        int Q = instance.getCapacity();

        List<Route> routes = new ArrayList<>();
        Route[] routeOf = new Route[n + 1];

        // initialization: one route per customer (node 1 is depot)
        for (int i = 2; i <= n; i++) {
            List<Integer> single = new ArrayList<>();
            single.add(i);
            Route r = new Route(single, instance);
            routes.add(r);
            routeOf[i] = r;
        }

        // compute all savings
        List<Saving> allSavings = new ArrayList<>();
        for (int i = 2; i <= n; i++) {
            for (int j = i + 1; j <= n; j++) {
                int s = instance.getDistance(1, i)
                        + instance.getDistance(1, j)
                        - instance.getDistance(i, j);
                allSavings.add(new Saving(i, j, s));
            }
        }

        while (true) {
            List<Saving> feasible = new ArrayList<>();
            int bestSaving = Integer.MIN_VALUE;

            // find all feasible savings
            for (Saving s : allSavings) {
                Route ri = routeOf[s.i];
                Route rj = routeOf[s.j];

                // already merged / same route
                if (ri == null || rj == null || ri == rj) {
                    continue;
                }

                // i and j must be endpoints
                if (!isEndCustomer(ri, s.i) || !isEndCustomer(rj, s.j)) {
                    continue;
                }

                // check capacity constraint
                int newLoad = ri.getDemand() + rj.getDemand();
                if (newLoad > Q) {
                    continue;
                }

                // feasible saving
                feasible.add(s);
                // update best saving
                if (s.value > bestSaving) {
                    bestSaving = s.value;
                }
            }

            if (feasible.isEmpty()) {
                break;
            }

            // build RCL based on alpha:
            // include all s with value >= (1 - alpha) * bestSaving
            double threshold = (1.0 - alpha) * bestSaving;
            List<Saving> rcl = new ArrayList<>();
            for (Saving s : feasible) {
                if (s.value >= threshold) {
                    rcl.add(s);
                }
            }

            if (rcl.isEmpty()) {
                break;
            }

            // randomly select a saving from RCL
            Saving chosen = rcl.get(rng.nextInt(rcl.size()));

            Route ri = routeOf[chosen.i];
            Route rj = routeOf[chosen.j];

            // merge routes ri and rj
            List<Integer> seqA = new ArrayList<>(ri.getCustomers());
            List<Integer> seqB = new ArrayList<>(rj.getCustomers());

            if (seqA.size() > 1 && seqA.get(0) == chosen.i) {
                Collections.reverse(seqA);
            }
            if (seqB.size() > 1 && seqB.get(seqB.size() - 1) == chosen.j) {
                Collections.reverse(seqB);
            }

            if (seqA.get(seqA.size() - 1) != chosen.i || seqB.get(0) != chosen.j) {
                continue;
            }

            List<Integer> mergedCustomers = new ArrayList<>(seqA);
            mergedCustomers.addAll(seqB);
            Route merged = new Route(mergedCustomers, instance);

            routes.remove(ri);
            routes.remove(rj);
            routes.add(merged);

            for (int c : mergedCustomers) {
                routeOf[c] = merged;
            }
        }
        return new CVRPSolution(routes);
    }

    /**
     * Checks if the given customer is at the start or end of the route.
     * @param r route
     * @param customer customer to check
     * @return true if customer is at start or end of route, false otherwise
     */
    private boolean isEndCustomer(Route r, int customer) {
        List<Integer> cust = r.getCustomers();
        if (cust.isEmpty()) return false;
        if (cust.size() == 1) return cust.get(0) == customer;
        return cust.get(0) == customer || cust.get(cust.size() - 1) == customer;
    }

    /**
     * Computes the total distance of the given solution.
     * @param sol CVRP solution
     * @param inst CVRP instance
     * @return total distance of the solution
     */
    private int computeSolutionDistance(CVRPSolution sol, CVRPInstance inst) {
        int total = 0;
        for (Route r : sol.getRoutes()) {
            total += r.getDistance(inst);
        }
        return total;
    }
}
