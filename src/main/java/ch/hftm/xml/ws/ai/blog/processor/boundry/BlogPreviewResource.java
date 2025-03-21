package ch.hftm.xml.ws.ai.blog.processor.boundry;

import ch.hftm.xml.ws.ai.blog.processor.entity.Blog;
import ch.hftm.xml.ws.ai.blog.processor.service.BlogHtmlGeneratorService;
import ch.hftm.xml.ws.ai.blog.processor.service.model.BlogService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/blog-preview")
public class BlogPreviewResource {

    private static final Logger LOG = Logger.getLogger(BlogPreviewResource.class);

    @Inject
    BlogService blogService;

    @Inject
    BlogHtmlGeneratorService blogHtmlGeneratorService;

    @GET
    @Path("/{blogId}")
    @Produces(MediaType.TEXT_HTML)
    public Response getBlogPreview(@PathParam("blogId") Long blogId) {
        Blog blog = blogService.getBlogById(blogId);
        if (blog == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("<h1>Blog not found</h1>")
                    .build();
        }

        String htmlContent = blogHtmlGeneratorService.generateHtml(blog);
        return Response.ok(htmlContent).build();
    }
}
