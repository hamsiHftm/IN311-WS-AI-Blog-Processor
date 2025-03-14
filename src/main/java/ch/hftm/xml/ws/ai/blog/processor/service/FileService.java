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
    private static final String JSON_SAVE_DIRECTORY = "data/output/json/";

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

    public String saveJsonFile(String jsonContent, Long recordId) throws IOException {
        File directory = new File(JSON_SAVE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs(); // Ensure directory exists
        }

        String cleanedJson = cleanJsonString(jsonContent);

        String fileName = recordId + ".json";
        File jsonFile = new File(JSON_SAVE_DIRECTORY, fileName);

        try (FileWriter writer = new FileWriter(jsonFile, StandardCharsets.UTF_8)) {
            writer.write(cleanedJson);
        }

        LOG.info("JSON file saved successfully: " + JSON_SAVE_DIRECTORY + fileName);
        return JSON_SAVE_DIRECTORY + fileName;
    }

    public String cleanJsonString(String aiGeneratedJson) {
        if (aiGeneratedJson == null || aiGeneratedJson.isBlank()) {
            return "{}"; // Return empty JSON object if input is invalid
        }

        // Remove leading ```json and trailing ```
        String cleanedJson = aiGeneratedJson.trim()
                .replaceAll("^```json", "")  // Remove the AI-starting markdown
                .replaceAll("```$", "");   // Remove the AI-ending markdown

        return cleanedJson.trim();
    }
}
