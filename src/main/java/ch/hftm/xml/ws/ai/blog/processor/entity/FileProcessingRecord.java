package ch.hftm.xml.ws.ai.blog.processor.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "file_processing_records")
public class FileProcessingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String htmlFilePath;  // Path to the uploaded HTML file
    private String jsonFilePath;  // Path to generated JSON file (AI output)
    private String jsonFileName;

    @Enumerated(EnumType.STRING)
    private ProcessingStatus status = ProcessingStatus.PENDING; // PENDING, PROCESSED, etc.

    @OneToOne
    @JoinColumn(name = "blog_id")
    private Blog blog; // Associated blog

    private LocalDateTime createdAt = LocalDateTime.now();
}
