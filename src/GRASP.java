package src;

import java.util.*;

public class GRASP {
    private final double alpha;
    private final VND vnd;
    private final Random rng = new Random(42);

    public GRASP(double alpha) {
        this.alpha = alpha;
        this.vnd = new VND();
    }

    public CVRPSolution solve(CVRPInstance instance, long timeLimitMillis) {
        long start = System.currentTimeMillis();

        CVRPSolution bestSol = null;
        int bestDist = Integer.MAX_VALUE;

        while (System.currentTimeMillis() - start < timeLimitMillis) {
            CVRPSolution sol = constructInitalSol(instance);
            sol = vnd.solve(sol, instance);

            int cost = computeSolutionCost(sol, instance);
            if (cost < bestDist) {
                bestDist = cost;
                bestSol = sol;
                System.out.println("--> NEW BEST SOLUTION: " + bestDist);
            }
        }
        return bestSol;
    }

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

    private CVRPSolution constructInitalSol(CVRPInstance instance) {
        int n = instance.getNodes();
        int Q = instance.getCapacity();

        List<Route> routes = new ArrayList<>();
        Route[] routeOf = new Route[n + 1];

        for (int i = 2; i <= n; i++) {
            List<Integer> single = new ArrayList<>();
            single.add(i);
            Route r = new Route(single, instance);
            routes.add(r);
            routeOf[i] = r;
        }

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

                int newLoad = ri.getDemand() + rj.getDemand();
                if (newLoad > Q) {
                    continue;
                }

                feasible.add(s);
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

            Saving chosen = rcl.get(rng.nextInt(rcl.size()));

            Route ri = routeOf[chosen.i];
            Route rj = routeOf[chosen.j];

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

    private boolean isEndCustomer(Route r, int customer) {
        List<Integer> cust = r.getCustomers();
        if (cust.isEmpty()) return false;
        if (cust.size() == 1) return cust.get(0) == customer;
        return cust.get(0) == customer || cust.get(cust.size() - 1) == customer;
    }

    private int computeSolutionCost(CVRPSolution sol, CVRPInstance inst) {
        int total = 0;
        for (Route r : sol.getRoutes()) {
            total += r.getDistance(inst);
        }
        return total;
    }
}
