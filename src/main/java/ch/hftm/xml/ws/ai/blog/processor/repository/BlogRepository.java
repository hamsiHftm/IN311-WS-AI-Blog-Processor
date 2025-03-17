package ch.hftm.xml.ws.ai.blog.processor.repository;

import ch.hftm.xml.ws.ai.blog.processor.entity.Blog;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BlogRepository implements PanacheRepository<Blog> {
}
