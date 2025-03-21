package ch.hftm.xml.ws.ai.blog.processor.service.openai;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.UUID;

@ApplicationScoped
public class OpenAIFileService {

    private static final Logger LOG = Logger.getLogger(OpenAIFileService.class);
    private static final String OPENAI_UPLOAD_URL = "https://api.openai.com/v1/files";

    @ConfigProperty(name = "quarkus.langchain4j.openai.api-key")
    String openAiApiKey;

    public String uploadFileToOpenAI(File file) throws IOException, InterruptedException {
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + file.getAbsolutePath());
        }

        LOG.info("Uploading file to OpenAI: " + file.getName());

        String boundary = "----Boundary" + UUID.randomUUID();

        var byteArray = buildMultipartData(file, boundary);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_UPLOAD_URL))
                .header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(byteArray))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            LOG.error("OpenAI file upload failed: " + response.body());
            throw new IOException("Failed to upload file to OpenAI. Status: " + response.statusCode());
        }

        String responseBody = response.body();
        LOG.info("OpenAI Upload Response: " + responseBody);

        // Extract the file ID from JSON response manually (you can use JSON parser if preferred)
        String fileId = extractFileIdFromResponse(responseBody);
        LOG.info("Received OpenAI File ID: " + fileId);
        return fileId;
    }

    private byte[] buildMultipartData(File file, String boundary) throws IOException {
        String fileName = file.getName();
        String CRLF = "\r\n";

        StringBuilder builder = new StringBuilder();
        builder.append("--").append(boundary).append(CRLF);
        builder.append("Content-Disposition: form-data; name=\"purpose\"").append(CRLF).append(CRLF);
        builder.append("user_data").append(CRLF);

        builder.append("--").append(boundary).append(CRLF);
        builder.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName).append("\"").append(CRLF);
        builder.append("Content-Type: application/pdf").append(CRLF).append(CRLF);

        byte[] fileBytes = java.nio.file.Files.readAllBytes(Path.of(file.getPath()));
        byte[] preamble = builder.toString().getBytes();
        byte[] closing = (CRLF + "--" + boundary + "--" + CRLF).getBytes();

        byte[] result = new byte[preamble.length + fileBytes.length + closing.length];
        System.arraycopy(preamble, 0, result, 0, preamble.length);
        System.arraycopy(fileBytes, 0, result, preamble.length, fileBytes.length);
        System.arraycopy(closing, 0, result, preamble.length + fileBytes.length, closing.length);

        return result;
    }

    private String extractFileIdFromResponse(String json) {
        // TODO convert into json then retrieve it from json
        int start = json.indexOf("\"id\":\"") + 6;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
