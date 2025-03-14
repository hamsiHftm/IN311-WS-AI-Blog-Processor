package ch.hftm.xml.ws.ai.blog.processor.boundry;

import ch.hftm.xml.ws.ai.blog.processor.service.ai.TestAIService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestQuery;

@Path("/files")
@Produces(MediaType.APPLICATION_JSON)
public class TestAIResource {

    @Inject
    TestAIService testAIService;

    @GET
    public String createBlog(@RestQuery String topic) {
        return testAIService.writeBlog(topic);
    }

}
