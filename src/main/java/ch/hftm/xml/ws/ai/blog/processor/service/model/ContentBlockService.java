package ch.hftm.xml.ws.ai.blog.processor.service.model;

import ch.hftm.xml.ws.ai.blog.processor.entity.ContentBlock;
import ch.hftm.xml.ws.ai.blog.processor.repository.ContentBlockRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ContentBlockService {

    @Inject
    ContentBlockRepository contentBlockRepository;

    @Transactional
    public void saveContentBlock(ContentBlock contentBlock) {
        contentBlockRepository.persist(contentBlock);
    }
}
