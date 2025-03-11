package ch.hftm.xml.ws.ai.blog.processor.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sections")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sectionTitle;  // Name of the section

    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog; // Each section belongs to a blog

    @ManyToOne
    @JoinColumn(name = "parent_section_id")
    private Section parentSection; // For nested sections

    @OneToMany(mappedBy = "parentSection", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Section> subSections; // List of nested sections

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ContentBlock> contentBlocks; // Each section contains multiple content blocks
}
