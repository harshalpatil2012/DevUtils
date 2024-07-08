import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
// add opencsv and poi-ooxml jar in the directory path
public class CSVComparer {

    public static void main(String[] args) {
        String filePath1 = "path/to/your/file1.csv";
        String filePath2 = "path/to/your/file2.csv";
        String outputFilePath = "path/to/your/different_records.csv";

        compareCSVFiles(filePath1, filePath2, outputFilePath);
    }

    public static void compareCSVFiles(String filePath1, String filePath2, String outputFilePath) {
        try (
            CSVReader reader1 = new CSVReader(new FileReader(filePath1));
            CSVReader reader2 = new CSVReader(new FileReader(filePath2));
            CSVWriter writer = new CSVWriter(new FileWriter(outputFilePath))
        ) {
            List<String[]> records1 = reader1.readAll();
            List<String[]> records2 = reader2.readAll();
            Set<String> set1 = new HashSet<>();
            Set<String> set2 = new HashSet<>();

            for (String[] record : records1) {
                String normalizedRecord = String.join(",", record).toLowerCase();
                set1.add(normalizedRecord);
            }

            for (String[] record : records2) {
                String normalizedRecord = String.join(",", record).toLowerCase();
                set2.add(normalizedRecord);
            }

            // Find records that are different
            Set<String> differentRecords = new HashSet<>(set1);
            differentRecords.addAll(set2);
            Set<String> commonRecords = new HashSet<>(set1);
            commonRecords.retainAll(set2);
            differentRecords.removeAll(commonRecords);

            // Write the different records to the output CSV file
            for (String record : differentRecords) {
                writer.writeNext(record.split(","));
            }

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }
}
