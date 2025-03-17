package ch.hftm.xml.ws.ai.blog.processor.service.model;

import ch.hftm.xml.ws.ai.blog.processor.entity.Section;
import ch.hftm.xml.ws.ai.blog.processor.repository.SectionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class SectionService {

    @Inject
    SectionRepository sectionRepository;

    @Transactional
    public void saveSections(List<Section> sections) {
        for (Section section : sections) {
            sectionRepository.persist(section);
        }
    }
}
