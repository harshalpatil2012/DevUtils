import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/file-processing")
public class FileProcessingController {

    private final FileProcessingService fileProcessingService;

    @Autowired
    public FileProcessingController(FileProcessingService fileProcessingService) {
        this.fileProcessingService = fileProcessingService;
    }

    @PostMapping("/process")
    public String processFiles(@RequestParam String file1Path, @RequestParam String file2Path) {
        try {
            fileProcessingService.removeDuplicatesAndCompareFiles(file1Path, file2Path);
            return "File processing completed successfully.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error during file processing: " + e.getMessage();
        }
    }
}


import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileProcessingService {

    public void removeDuplicatesAndCompareFiles(String file1Path, String file2Path) throws IOException {
        File file1 = new File(file1Path);
        File file2 = new File(file2Path);
        Path tempFile = Files.createTempFile("uniqueRecords", ".txt");

        try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
            Files.lines(file1.toPath()).parallel().distinct().forEach(line -> {
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        try (BufferedReader reader2 = Files.newBufferedReader(file2.toPath());
             Stream<String> tempFileLines = Files.lines(tempFile)) {
             
            Set<String> linesInFile2 = reader2.lines().collect(Collectors.toSet());
            List<String> filteredLines = tempFileLines.parallel()
                                                .filter(line -> !linesInFile2.contains(line))
                                                .collect(Collectors.toList());

            Files.write(file1.toPath(), filteredLines);
        }

        Files.deleteIfExists(tempFile);
    }
}
