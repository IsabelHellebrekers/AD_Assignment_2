package src;

/**
 * Variable Neighborhood Descent (VND) algorithm for improving CVRP solutions.
 */
public class VND {
    private final NeighbourhoodReal nbh = new NeighbourhoodReal();

    /**
     * Improves the given solution using VND.
     * @param sol solution to improve
     * @param instance CVRP instance
     * @return improved solution
     */
    public CVRPSolution solve(CVRPSolution sol, CVRPInstance instance) {
        boolean improved = true;

        while (improved) {
            improved = false;

            // Try reocation moves within the same route
            if (nbh.relocate(sol.getRoutes(), instance)) {
                improved = true;
                continue;
            }

            // Try swap moves within the same route
            if (nbh.intraRouteSwap(sol.getRoutes(), instance)) {
                improved = true;
                continue;
            }

            // Try 2-opt moves within the same route
            if (nbh.opt2Swap(sol.getRoutes(), instance)) {
                improved = true;
                continue;
            }

            // Try relocation moves between different routes
            if (nbh.interRouteRelocate(sol.getRoutes(), instance)) {
                improved = true;
                continue;
            }

            // Try swap moves between different routes
            if (nbh.interRouteSwap(sol.getRoutes(), instance)) {
                improved = true;
                continue;
            }
        }

        return sol;
    }
}
