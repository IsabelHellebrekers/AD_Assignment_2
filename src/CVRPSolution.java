package src;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a solution to a CVRP instance
 */
public class CVRPSolution {
    private final List<Route> routes = new ArrayList<>();

    public CVRPSolution() {
    }
    
    public CVRPSolution(List<Route> routes) {
        this.routes.addAll(routes);
    }
    
    /**
     * Adds a route to the solution
     * @param route the route to add
     */
    public void addRoute(Route route) {
        routes.add(route);
    }

    /**
     * Gets the list of routes in the solution
     * @return the list of routes
     */
    public List<Route> getRoutes() {
        return routes;
    }

    /**
     * Gets the number of routes in the solution
     * @return the number of routes
     */
    public int getNumberOfRoutes() {
        return routes.size();
    }

    /**
     * Gets the total distance of the solution
     * @param instance CVRP instance 
     * @return the total distance
     */
    public int getTotalDistance(CVRPInstance instance) {
        int totDist = 0;
        for (Route r : routes) {
            totDist += r.getDistance(instance);
        }
        return totDist;
    }

    /**
     * Checks if the solution is feasible for the given CVRP instance
     * @param instance CVRP instance
     * @return true if the solution is feasible, false otherwise
     */
    public boolean isFeasible(CVRPInstance instance) {
        for (Route r : routes) {
            if (r.getDemand() > instance.getCapacity()) {
                return false;
            }
        }
        return true;
    }
}
