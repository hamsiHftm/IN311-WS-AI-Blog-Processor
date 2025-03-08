package ch.hftm.xml.ws.ai.blog.processor.entity;

public enum ProcessingStatus {
    PENDING,     // The HTML file is uploaded but not processed yet
    PROCESSING,  // The AI is currently processing the file
    PROCESSED,   // The JSON is generated and ready for parsing
    STORED,      // The structured content is successfully stored in the database
    FAILED       // If any process failed
}
