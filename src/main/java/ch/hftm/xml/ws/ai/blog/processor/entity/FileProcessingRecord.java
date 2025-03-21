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

    private String filePath;  // Renamed from htmlFilePath (Can be HTML or PDF)
    private String openAIFileId;
    private String jsonFilePath;  // Path to generated JSON file (AI output)

    @Enumerated(EnumType.STRING)
    private ProcessingStatus status = ProcessingStatus.UPLOADING; // PENDING, PROCESSED, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType fileType;  // NEW: Defines if file is HTML or PDF

    @OneToOne
    @JoinColumn(name = "blog_id")
    private Blog blog; // Associated blog

    private LocalDateTime createdAt = LocalDateTime.now();
}
