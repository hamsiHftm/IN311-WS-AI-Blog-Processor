package ch.hftm.xml.ws.ai.blog.processor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "content_blocks")
public class ContentBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section; // Content belongs to a section

    @Enumerated(EnumType.STRING)
    private ContentType type; // HEADING, PARAGRAPH, IMAGE, LIST, TABLE, etc.

    @Column(columnDefinition = "TEXT")
    private String value; // Actual content (text, image URL, etc.)

    private int orderIndex; // Order of content inside section

    private Integer level; // For headings (1-6), optional

    private String listType; // "ordered" or "unordered", optional for lists

    @Column(columnDefinition = "TEXT")
    private String items; // Stores JSON array for list items

    @Column(columnDefinition = "TEXT")
    private String subItems; // Stores nested list items

    @Column(columnDefinition = "TEXT")
    private String columns; // Stores JSON array for table column names

    @Column(columnDefinition = "TEXT")
    private String rows; // Stores JSON array for table rows
}
