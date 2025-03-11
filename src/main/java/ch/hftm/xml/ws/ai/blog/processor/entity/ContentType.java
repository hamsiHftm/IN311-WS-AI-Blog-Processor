package ch.hftm.xml.ws.ai.blog.processor.entity;

public enum ContentType {
    HEADING,      // Main heading (h1)
    PARAGRAPH,    // Paragraph text
    IMAGE,        // Image URL
    LIST,         // Ordered/unordered list
    TABLE,        // Table (stored as JSON inside "rows" & "columns")
    SECTION       // Nested section
}
