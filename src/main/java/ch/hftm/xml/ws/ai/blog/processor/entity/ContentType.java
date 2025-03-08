package ch.hftm.xml.ws.ai.blog.processor.entity;

public enum ContentType {
    HEADING,      // Main heading (h1)
    SUBHEADING,   // Subheading (h2, h3, etc.)
    PARAGRAPH,    // Paragraph text
    IMAGE,        // Image URL
    LIST_ITEM,    // List item in an ordered/unordered list
    TABLE,        // Table (stored as JSON or CSV format inside "content")
    CODE_BLOCK    // Code snippet
}
