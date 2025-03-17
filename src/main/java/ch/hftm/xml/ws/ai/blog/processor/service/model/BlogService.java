package ch.hftm.xml.ws.ai.blog.processor.service.model;

import ch.hftm.xml.ws.ai.blog.processor.entity.Blog;
import ch.hftm.xml.ws.ai.blog.processor.repository.BlogRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BlogService {

    @Inject
    BlogRepository blogRepository;

    @Transactional
    public void saveBlog(Blog blog) {
        blogRepository.persist(blog);
    }

    public Blog getBlogById(Long blogId) {
        return blogRepository.findById(blogId);
    }
}
