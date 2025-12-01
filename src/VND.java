package src;

public class VND {

    private final NeighbourhoodReal nbh = new NeighbourhoodReal();

    public CVRPSolution solve(CVRPSolution sol, CVRPInstance instance) {

        boolean improved = true;
        while (improved) {
            improved = false;

            if (nbh.relocate(sol.getRoutes(), instance)) {
                improved = true;
                continue;
            }

            if (nbh.intraRouteSwap(sol.getRoutes(), instance)) {
                improved = true;
                continue;
            }

            if (nbh.opt2Swap(sol.getRoutes(), instance)) {
                improved = true;
                continue;
            }

            if (nbh.interRouteRelocate(sol.getRoutes(), instance)) {
                improved = true;
                continue;
            }

            if (nbh.interRouteSwap(sol.getRoutes(), instance)) {
                improved = true;
                continue;
            }
        }

        return sol;
    }
}
