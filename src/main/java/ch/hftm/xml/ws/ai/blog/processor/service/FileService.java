package ch.hftm.xml.ws.ai.blog.processor.service;

import ch.hftm.xml.ws.ai.blog.processor.entity.FileProcessingRecord;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class FileService {

    private static final Logger LOG = Logger.getLogger(FileService.class);
    private static final String JSON_SAVE_DIRECTORY = "/opt/app/json_output/";

    public boolean validateFile(String filePath,String fileType) throws Exception {
        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        if (!filePath.toLowerCase().endsWith(fileType)) {
            throw new IllegalArgumentException("Invalid file type. Only "+ fileType + " files are allowed.");
        }

        if (!Files.isReadable(file.toPath())) {
            throw new IOException("File cannot be read: " + filePath);
        }

        LOG.info("File validation successful: " + filePath);
        return true;
    }

    public String readHtmlFile(FileProcessingRecord record) throws IOException {
        if (record == null || record.getHtmlFilePath() == null || record.getHtmlFilePath().isBlank()) {
            throw new IllegalArgumentException("Invalid FileProcessingRecord: HTML file path is missing.");
        }

        Path filePath = Path.of(record.getHtmlFilePath());
        File file = filePath.toFile();

        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("HTML file not found: " + record.getHtmlFilePath());
        }

        if (!Files.isReadable(filePath)) {
            throw new IOException("HTML file cannot be read: " + record.getHtmlFilePath());
        }

        LOG.info("Reading HTML file: " + record.getHtmlFilePath());

        return Files.readString(filePath, StandardCharsets.UTF_8);
    }

    public String saveJsonFile(String jsonContent, String jsonDirectory, Long recordId) throws IOException {
        File directory = new File(jsonDirectory);
        if (!directory.exists()) {
            directory.mkdirs(); // Ensure directory exists
        }

        String filePath = jsonDirectory + "record_" + recordId + ".json";
        File jsonFile = new File(filePath);

        try (FileWriter writer = new FileWriter(jsonFile, StandardCharsets.UTF_8)) {
            writer.write(jsonContent);
        }

        LOG.info("JSON file saved successfully: " + filePath);
        return filePath;
    }
}
