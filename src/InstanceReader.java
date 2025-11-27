package src;
import java.io.*;

/**
 * This class reads a CVRP instance from a txt file
 */
public class InstanceReader {
    public static CVRPInstance read(String filePath) throws IOException {
        int nodes = 0;
        int capacity = 0;

        int[] x = null;
        int[] y = null;
        int[] demand = null;

        boolean inCoords = false;
        boolean inDemand = false;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith("DIMENSION")) {
                    String[] parts = line.split(":");
                    nodes = Integer.parseInt(parts[1].trim());
                } else if (line.startsWith("CAPACITY")) {
                    String[] parts = line.split(":");
                    capacity = Integer.parseInt(parts[1].trim());
                } else if (line.startsWith("NODE_COORD")) {
                    inCoords = true;
                    inDemand = false;
                    x = new int[nodes + 1];
                    y = new int[nodes + 1];
                } else if (line.startsWith("DEMAND")) {
                    inCoords = false;
                    inDemand = true;
                    demand = new int[nodes + 1];
                } else if (inCoords) {
                    String[] parts = line.split("\\s+");
                    int id = Integer.parseInt(parts[0]);
                    int xi = Integer.parseInt(parts[1]);
                    int yi = Integer.parseInt(parts[2]);
                    x[id] = xi;
                    y[id] = yi;
                } else if (inDemand) {
                    String[] parts = line.split("\\s+");
                    int id = Integer.parseInt(parts[0]);
                    int di = Integer.parseInt(parts[1]);
                    demand[id] = di;
                }
            }
        }
        return new CVRPInstance(nodes, capacity, x, y, demand);
    }
}
