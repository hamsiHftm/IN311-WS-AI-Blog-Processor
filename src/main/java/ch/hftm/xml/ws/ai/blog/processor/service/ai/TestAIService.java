package ch.hftm.xml.ws.ai.blog.processor.service.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkus.security.User;

@RegisterAiService
public interface TestAIService {

    @SystemMessage("You are a blog content creator.")
    @UserMessage("""
            Write a short blog about {topic}
            """)
    String writeBlog(String topic);
}
