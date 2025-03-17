package ch.hftm.xml.ws.ai.blog.processor.entity;

public enum ContentType {
    HEADING,      // Main heading (h1)
    PARAGRAPH,    // Paragraph text
    IMAGE,        // Image URL
    LIST,         // Ordered/unordered list
    TABLE,        // Table (stored as JSON inside "rows" & "columns")
    SECTION;       // Nested section

    public static ContentType fromString(String type) {
        try {
            return ContentType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // Return null for unknown types (or handle differently)
        }
    }
}
