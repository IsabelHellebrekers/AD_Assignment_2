package src;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements various neighborhood structures for CVRP solutions.
 */
public class NeighbourhoodReal {

    /**
     * Tries to improve the solution by swapping two customers within the same route.
     * @param routes list of routes in the solution
     * @param instance CVRP instance
     * @return true if an improvement was made, false otherwise
     */
    public boolean intraRouteSwap(List<Route> routes, CVRPInstance instance) {
        int bestImprovement = 0;
        int bestIndex = -1;
        int bestI = -1;
        int bestJ = -1;

        // explore all routes
        for (int index = 0; index < routes.size(); index++) {
            Route route = routes.get(index);
            List<Integer> customers = route.getCustomers();
            if (customers.size() < 2) {
                continue;
            }
            int distPrev = route.getDistance(instance);

            // explore all pairs of customers to swap
            for (int i = 0; i < customers.size() - 1; i++) {
                for (int j = i + 1; j < customers.size(); j++) {
                    List<Integer> customersUpdated = new ArrayList<>(customers);

                    int temp = customersUpdated.get(i);
                    customersUpdated.set(i, customersUpdated.get(j));
                    customersUpdated.set(j, temp);

                    int distNew = RouteUtils.computeRouteDistance(customersUpdated, instance);
                    if (distNew - distPrev < bestImprovement) {
                        bestImprovement = distNew - distPrev;
                        bestIndex = index;
                        bestI = i;
                        bestJ = j;
                    }
                }
            }
        }
        if (bestImprovement >= 0) {
            return false;
        }
        // apply the best swap found
        Route route = routes.get(bestIndex);
        List<Integer> customers = new ArrayList<>(route.getCustomers());
        int temp = customers.get(bestI);
        customers.set(bestI, customers.get(bestJ));
        customers.set(bestJ, temp);

        Route newRoute = new Route(customers, instance);
        routes.set(bestIndex, newRoute);
        return true;
    }

    /**
     * Tries to improve the solution by relocating a customer within the same route.
     * @param routes list of routes in the solution
     * @param instance CVRP instance
     * @return true if an improvement was made, false otherwise
     */
    public boolean relocate(List<Route> routes, CVRPInstance instance) {
        int bestImprovement = 0;
        int bestIndex = -1;
        int bestI = -1;
        int bestJ = -1;

        // explore all routes
        for (int index = 0; index < routes.size(); index++) {
            Route route = routes.get(index);
            List<Integer> customers = route.getCustomers();
            if ( customers.size() < 2) {
                continue;
            }

            int distPrev = route.getDistance(instance);

            // explore all possible relocations
            for (int from = 0; from <  customers.size(); from++) {
                for (int to = 0; to <=  customers.size(); to++) {
                    if (to == from || to == from + 1) {
                        continue;
                    }

                    List<Integer> customersUpdated = new ArrayList<>(customers);
                    int customer = customersUpdated.remove(from);
                    int adjustedTo = to;
                    if (to > from) {
                        adjustedTo--;
                    }
                    customersUpdated.add(adjustedTo, customer);

                    int newDist = RouteUtils.computeRouteDistance(customersUpdated, instance);
                    int delta = newDist - distPrev;

                    if (delta < bestImprovement) {
                        bestImprovement = delta;
                        bestIndex = index;
                        bestI = from;
                        bestJ = to;
                    }
                }
            }
        }

        if (bestImprovement >= 0) {
            return false;
        }

        // apply the best relocation found
        Route route = routes.get(bestIndex);
        List<Integer> customers = new ArrayList<>(route.getCustomers());
        int customer = customers.remove(bestI);
        int adjustedTo = bestJ;
        if (bestJ > bestI) {
            adjustedTo--;
        }
        customers.add(adjustedTo, customer);
        Route newRoute = new Route(customers, instance);
        routes.set(bestIndex, newRoute);

        return true;
    }

    /**
     * Tries to improve the solution by performing a 2-opt move within the same route.
     * @param routes list of routes in the solution
     * @param instance CVRP instance
     * @return true if an improvement was made, false otherwise.
     */
    public boolean opt2Swap(List<Route> routes, CVRPInstance instance) {
        int bestImprovement = 0;
        int bestIndex = -1;
        int bestI = -1;
        int bestJ = -1;

        // explore all routes
        for (int index = 0; index < routes.size(); index++) {
            Route route = routes.get(index);
            List<Integer> customers = route.getCustomers();
            if (customers.size() < 2) continue;

            int distPrev = route.getDistance(instance);

            // explore all pairs of positions to reverse
            for (int x = 0; x < customers.size() - 1; x++) {
                for (int z = x + 1; z < customers.size(); z++) {

                    List<Integer> customersUpdated = new ArrayList<>(customers);

                    int left = x;
                    int right = z;
                    while (left < right) {
                        int tmp = customersUpdated.get(left);
                        customersUpdated.set(left, customersUpdated.get(right));
                        customersUpdated.set(right, tmp);
                        left++;
                        right--;
                    }

                    int distNew = RouteUtils.computeRouteDistance(customersUpdated, instance);

                    if (distNew - distPrev < bestImprovement) {
                        bestImprovement = distNew - distPrev;
                        bestIndex = index;
                        bestI = x;
                        bestJ = z;
                    }
                }
            }
        }

        if (bestImprovement >= 0) {
            return false;
        }

        // apply the best 2-opt found
        Route route = routes.get(bestIndex);
        List<Integer> customers = new ArrayList<>(route.getCustomers());
        int left = bestI;
        int right = bestJ;
        while (left < right) {
            int tmp = customers.get(left);
            customers.set(left, customers.get(right));
            customers.set(right, tmp);
            left++;
            right--;
        }
        Route newRoute = new Route(customers, instance);
        routes.set(bestIndex, newRoute);
        return true;
    }

    /**
     * Tries to improve the solution by relocating a customer between different routes.
     * @param routes list of routes in the solution
     * @param instance CVRP instance
     * @return true if an improvement was made, false otherwise
     */
    public boolean interRouteRelocate(List<Route> routes, CVRPInstance instance) {
        int capacity = instance.getCapacity();

        int bestDelta = 0; 
        int bestFromRoute = -1;
        int bestToRoute = -1;
        int bestPosFrom = -1;
        int bestPosTo = -1;

        // explore all pairs of routes
        for (int rFrom = 0; rFrom < routes.size(); rFrom++) {
            Route routeFrom = routes.get(rFrom);
            List<Integer> custFrom = routeFrom.getCustomers();
            if (custFrom.isEmpty()) continue;

            int oldDistFrom = routeFrom.getDistance(instance);

            for (int rTo = 0; rTo < routes.size(); rTo++) {
                if (rTo == rFrom) continue;  // inter-route only

                Route routeTo = routes.get(rTo);
                List<Integer> custTo = routeTo.getCustomers();

                int oldDistTo = routeTo.getDistance(instance);

                // explore all customers in routeFrom
                for (int posFrom = 0; posFrom < custFrom.size(); posFrom++) {
                    int customer = custFrom.get(posFrom);
                    int demandCustomer = instance.getDemand(customer);

                    int newDemandFrom = routeFrom.getDemand() - demandCustomer;
                    int newDemandTo = routeTo.getDemand() + demandCustomer;

                    // check capacity constraints
                    if (newDemandFrom > capacity || newDemandTo > capacity) {
                        continue;
                    }

                    // explore all possible insertion positions in routeTo
                    for (int posTo = 0; posTo <= custTo.size(); posTo++) {
                        List<Integer> newFrom = new ArrayList<>(custFrom);
                        newFrom.remove(posFrom);

                        List<Integer> newTo = new ArrayList<>(custTo);
                        newTo.add(posTo, customer);

                        int newDistFrom = RouteUtils.computeRouteDistance(newFrom, instance);
                        int newDistTo = RouteUtils.computeRouteDistance(newTo, instance);

                        int delta = (newDistFrom + newDistTo) - (oldDistFrom + oldDistTo);

                        // update best improvement found so far
                        if (delta < bestDelta) {
                            bestDelta = delta;
                            bestFromRoute = rFrom;
                            bestToRoute = rTo;
                            bestPosFrom = posFrom;
                            bestPosTo = posTo;
                        }
                    }
                }
            }
        }

        if (bestDelta >= 0) {
            return false;
        }

        // apply the best relocation found
        Route routeFrom = routes.get(bestFromRoute);
        Route routeTo = routes.get(bestToRoute);

        List<Integer> fromCust = new ArrayList<>(routeFrom.getCustomers());
        List<Integer> toCust = new ArrayList<>(routeTo.getCustomers());

        int customer = fromCust.remove(bestPosFrom);
        toCust.add(bestPosTo, customer);

        routes.set(bestFromRoute, new Route(fromCust, instance));
        routes.set(bestToRoute, new Route(toCust, instance));

        return true;
    }

    /**
     * Tries to improve the solution by swapping customers between different routes.
     * @param routes list of routes in the solution
     * @param instance CVRP instance
     * @return true if an improvement was made, false otherwise
     */
    public boolean interRouteSwap(List<Route> routes, CVRPInstance instance) {
    int capacity = instance.getCapacity();

    int bestDelta = 0;
    int bestRouteA = -1;
    int bestRouteB = -1;
    int bestPosA = -1;
    int bestPosB = -1;

    // explore all pairs of routes
    for (int rA = 0; rA < routes.size(); rA++) {
        Route routeA = routes.get(rA);
        List<Integer> custA = routeA.getCustomers();
        if (custA.isEmpty()) continue;

        int distAOld = routeA.getDistance(instance);

        for (int rB = rA + 1; rB < routes.size(); rB++) {  
            Route routeB = routes.get(rB);
            List<Integer> custB = routeB.getCustomers();
            if (custB.isEmpty()) continue;

            int distBOld = routeB.getDistance(instance);

            // explore all pairs of customers to swap
            for (int posA = 0; posA < custA.size(); posA++) {
                int customerA = custA.get(posA);
                int demandA = instance.getDemand(customerA);

                for (int posB = 0; posB < custB.size(); posB++) {
                    int customerB = custB.get(posB);
                    int demandB = instance.getDemand(customerB);

                    int newLoadA = routeA.getDemand() - demandA + demandB;
                    int newLoadB = routeB.getDemand() - demandB + demandA;

                    if (newLoadA > capacity || newLoadB > capacity) {
                        continue;
                    }

                    List<Integer> newA = new ArrayList<>(custA);
                    List<Integer> newB = new ArrayList<>(custB);

                    newA.set(posA, customerB);
                    newB.set(posB, customerA);

                    int distANew = RouteUtils.computeRouteDistance(newA, instance);
                    int distBNew = RouteUtils.computeRouteDistance(newB, instance);

                    int delta = (distANew + distBNew) - (distAOld + distBOld);

                    // update best improvement found so far
                    if (delta < bestDelta) {
                        bestDelta = delta;
                        bestRouteA = rA;
                        bestRouteB = rB;
                        bestPosA = posA;
                        bestPosB = posB;
                    }
                }
            }
        }
    }

    if (bestDelta >= 0) {
        return false; 
    }

    // apply the best swap found
    Route routeA = routes.get(bestRouteA);
    Route routeB = routes.get(bestRouteB);

    List<Integer> custA = new ArrayList<>(routeA.getCustomers());
    List<Integer> custB = new ArrayList<>(routeB.getCustomers());

    int customerA = custA.get(bestPosA);
    int customerB = custB.get(bestPosB);

    custA.set(bestPosA, customerB);
    custB.set(bestPosB, customerA);

    routes.set(bestRouteA, new Route(custA, instance));
    routes.set(bestRouteB, new Route(custB, instance));

    return true;
}
}
