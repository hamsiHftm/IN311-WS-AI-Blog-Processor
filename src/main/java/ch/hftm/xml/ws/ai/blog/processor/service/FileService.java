package ch.hftm.xml.ws.ai.blog.processor.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

@ApplicationScoped
public class FileService {

    private static final Logger LOG = Logger.getLogger(FileService.class);

    public boolean validateFile(String filePath) throws Exception {
        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        if (!filePath.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Invalid file type. Only .pdf files are allowed.");
        }

        if (!Files.isReadable(file.toPath())) {
            throw new IOException("File cannot be read: " + filePath);
        }

        LOG.info("File validation successful: " + filePath);
        return true;
    }
}
