import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
/**
*java -version
*javac RemoveDuplicates.java
*java RemoveDuplicates
*/

public class RemoveDuplicates {

    public static void main(String[] args) {
        String[] inputFiles = { "C:/Logs/20millionloc.csv" }; // List of input files
        // String[] inputFiles = { "C:/Logs/5m-records.csv" }; // List of input files
        // String[] inputFiles = { "C:/Logs/500000-Sales-Records.csv" }; // List of input files
        int numThreads = Runtime.getRuntime().availableProcessors(); // Number of CPU cores for parallel processing

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        for (String inputFile : inputFiles) {
            executor.submit(new RemoveDuplicatesTask(inputFile));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class RemoveDuplicatesTask implements Callable<Void> {
        private final String inputFile;

        public RemoveDuplicatesTask(String inputFile) {
            this.inputFile = inputFile;
        }

        @Override
        public Void call() throws Exception {
            long startTime = System.currentTimeMillis();
            String outputFolder = "C:/updates";
            Path outputPath = Paths.get(outputFolder, Paths.get(inputFile).getFileName().toString().replaceFirst("[.][^.]+$", "_unique.txt"));
            int totalRecords = 0;
            int totalUniqueRecords = 0;
            int totalDuplicateRecords = 0;
            Set<String> seen = new HashSet<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toString()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    totalRecords++;
                    if (!seen.contains(line)) {
                        seen.add(line);
                        writer.write(line);
                        writer.newLine();
                        totalUniqueRecords++;
                    } else {
                        totalDuplicateRecords++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            long endTime = System.currentTimeMillis();
            double executionTime = (endTime - startTime) / 1000.0;

            // Print counts
            System.out.println("Total records: " + totalRecords);
            System.out.println("Total unique records: " + totalUniqueRecords);
            System.out.println("Total duplicate records: " + totalDuplicateRecords);
            System.out.println("Total execution time: " + executionTime + " seconds");
            System.out.println("Unique records file generated: " + outputPath);

            return null;
        }
    }
}
