package src;

/**
 * This class represents a CVRP instance
 */
public class CVRPInstance {
    private final int nodes;
    private final int capacity;
    private final int[] x;
    private final int[] y;
    private final int[] demand;
    private final int[][] distance;

    /**
     * Constructor
     * @param nodes customers + depot (node 1)
     * @param capacity vehicle capacity
     * @param x x-coordinates
     * @param y y-coordinates
     * @param demand demand of each node
     */
    public CVRPInstance(int nodes, int capacity, int[] x, int[] y, int[] demand) {
        this.nodes = nodes;
        this.capacity = capacity;
        this.x = x;
        this.y = y;
        this.demand = demand;
        this.distance = computeDistanceMatrix();
    }

    /**
     * Compute distance matrix (Euclidean distance rounded to nearest integer)
     * @return distance matrix
     */
    private int[][] computeDistanceMatrix() {
        int[][] d = new int[nodes + 1][nodes + 1];
        for (int i = 1; i <= nodes; i++) {
            for (int j = 1; j <= nodes; j++) {
                int dx = x[i] - x[j];
                int dy = y[i] - y[j];
                double dist = Math.sqrt(dx * dx + dy * dy);
                d[i][j] = (int) Math.round(dist);
            }
        }
        return d;
    }

    /**
     * Gets number of nodes
     * @return number of nodes
     */
    public int getNodes() {
        return nodes;
    }

    /**
     * Gets vehicle capacity
     * @return vehicle capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets x-coordinate of node i
     * @param i node index
     * @return x-coordinate
     */
    public int getX(int i) {
        return x[i];
    }

    /**
     * Gets y-coordinate of node i  
     * @param i node index
     * @return y-coordinate
     */
    public int getY(int i) {
        return y[i];
    }

    /**
     * Gets demand of node i
     * @param i node index
     * @return demand
     */
    public int getDemand(int i) {
        return demand[i];
    }

    /**
     * Gets distance between node i and j (Euclidean distance rounded to nearest integer)
     * @param i node index (from)
     * @param j node index (to)
     * @return distance from node i to j
     */
    public int getDistance(int i, int j) {
        return distance[i][j];
    }
}
