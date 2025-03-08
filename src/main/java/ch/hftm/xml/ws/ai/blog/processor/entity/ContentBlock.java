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
    private ContentType type; // HEADING, PARAGRAPH, IMAGE, etc.

    @Column(columnDefinition = "TEXT")
    private String content; // Actual content or image URL

    private int orderIndex; // Order of content inside section
}
