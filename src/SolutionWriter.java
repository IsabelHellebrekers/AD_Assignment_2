package src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Utility class to write a CVRP solution to a txt file.
 */
public class SolutionWriter {

    public static void writeSolution(String filename, CVRPSolution sol) throws IOException {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {

            for (Route r : sol.getRoutes()) {
                List<Integer> customers = r.getCustomers();

                for (int i = 0; i < customers.size(); i++) {
                    bw.write(Integer.toString(customers.get(i)));
                    if (i < customers.size() - 1) {
                        bw.write(" ");
                    }
                }
                bw.newLine();
            }
        }
    }
}
