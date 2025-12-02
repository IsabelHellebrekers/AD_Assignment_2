package src;

import java.util.List;

/**
 * Utility class to compute route distances.
 */
public class RouteUtils {
    public static int computeRouteDistance(List<Integer> customers, CVRPInstance instance) {
        Route routeCurr = new Route(customers, instance);
        return routeCurr.getDistance(instance);
    }
}
