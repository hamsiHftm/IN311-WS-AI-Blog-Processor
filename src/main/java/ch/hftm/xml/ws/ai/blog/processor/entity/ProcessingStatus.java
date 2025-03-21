package ch.hftm.xml.ws.ai.blog.processor.entity;

public enum ProcessingStatus {
    UPLOADING,   // File is being uploaded to OpenAI / DB
    UPLOADED,    // File is uploaded successfully, waiting for processing
    PROCESSING,  // AI is extracting structured content
    PROCESSED,   // JSON file is generated and ready
    STORED,      // Data is stored in the database
    FAILED       // Any failure during the process
}
