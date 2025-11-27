package src;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * This class represents a route in a CVRP solution.
 */
public class Route {
    private final List<Integer> customers = new ArrayList<>();
    private int demand = 0;

    public Route() {
    }

    /**
     * Adds a customer to the end of the route and updates the demand.
     * @param customer customer to add
     * @param instance CVRP instance from which to get the demand
     */
    public void addCustomerToEnd(int customer, CVRPInstance instance) {
        customers.add(customer);
        demand += instance.getDemand(customer);
    }

    /**
     * Adds a customer to the start of the route and updates the demand.
     * @param customer customer to add
     * @param instance CVRP instance from which to get the demand
     */
    public void addCustomerToStart(int customer, CVRPInstance instance) {
        customers.add(0, customer);
        demand += instance.getDemand(customer);
    }

    /**
     * Checks if the route contains a specific customer.
     * @param customer customer to check
     * @return true if the customer is in the route, false otherwise
     */
    public boolean contains(int customer) {
        return customers.contains(customer);
    }

    /**
     * Gets the first customer in the route.
     * @return the first customer, or -1 if the route is empty
     */
    public int getFirstCustomer() {
        if (customers.isEmpty()) {
            return -1;
        } else {
            return customers.get(0);
        }
    }

    /**
     * Gets the last customer in the route.
     * @return the last customer, or -1 if the route is empty
     */
    public int getLastCustomer() {
        if (customers.isEmpty()) {
            return -1;
        } else {
            return customers.get(customers.size() - 1);
        }
    }

    /**
     * Gets the total demand of the route.
     * @return the total demand of the route
     */
    public int getDemand() {
        return demand;
    }

    /**
     * Gets the list of customers in the route.
     * @return the list of customers
     */
    public List<Integer> getCustomers() {
        return customers;
    }

    /**
     * Reverses the order of customers in the route.
     */
    public void reverse() {
        Collections.reverse(customers);
    }

    /**
     * Gets the total distance of the route (Euclidean), including return to depot.
     * @param instance CVRP instance from which to get distances
     * @return the total distance of the route
     */
    public int getDistance(CVRPInstance instance) {
        if (customers.isEmpty()) {
            return 0;
        }
        int depot = 1;
        int totDist = 0;
        int prev = depot;

        for (int c : customers) {
            totDist += instance.getDistance(prev, c);
            prev = c;
        }

        totDist += instance.getDistance(prev, depot);
        return totDist;
    }
}
