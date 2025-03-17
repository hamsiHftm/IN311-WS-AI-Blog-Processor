package ch.hftm.xml.ws.ai.blog.processor.service;

import ch.hftm.xml.ws.ai.blog.processor.entity.FileProcessingRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.jboss.logging.Logger;

import java.io.*;
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
        if (record == null || record.getFilePath() == null || record.getFilePath().isBlank()) {
            throw new IllegalArgumentException("Invalid FileProcessingRecord: HTML file path is missing.");
        }

        Path filePath = Path.of(record.getFilePath());
        File file = filePath.toFile();

        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("HTML file not found: " + record.getFilePath());
        }

        if (!Files.isReadable(filePath)) {
            throw new IOException("HTML file cannot be read: " + record.getFilePath());
        }

        LOG.info("Reading HTML file: " + record.getFilePath());

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

    public JsonObject readJsonFile(FileProcessingRecord record) throws Exception {
        File jsonFile = new File(record.getJsonFilePath());
        String jsonContent = Files.readString(jsonFile.toPath());

        try (JsonReader reader = Json.createReader(new StringReader(jsonContent))) {
            return reader.readObject();
        }
    }
}
