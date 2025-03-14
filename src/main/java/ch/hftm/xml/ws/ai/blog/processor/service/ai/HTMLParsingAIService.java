package ch.hftm.xml.ws.ai.blog.processor.service.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface HTMLParsingAIService {

    @SystemMessage("You are an AI-powered HTML parser that extracts structured blog content from raw HTML.")
    @UserMessage("""
            Extract structured blog content from the provided HTML input and return a JSON structure 
            following this format:

            {
              "title": "Extracted Blog Title",
              "content": [
                {
                  "type": "HEADING",
                  "level": 1,
                  "value": "Introduction",
                  "orderIndex": 1
                },
                {
                  "type": "PARAGRAPH",
                  "value": "This is an introduction paragraph providing an overview of the blog.",
                  "orderIndex": 2
                },
                {
                  "type": "SECTION",
                  "orderIndex": 3,
                  "content": [
                    {
                      "type": "HEADING",
                      "level": 2,
                      "value": "Main Section",
                      "orderIndex": 4
                    },
                    {
                      "type": "PARAGRAPH",
                      "value": "This section contains important details.",
                      "orderIndex": 5
                    },
                    {
                      "type": "LIST",
                      "listType": "unordered",
                      "items": [
                        "First list item",
                        "Second list item",
                        "Third list item"
                      ],
                      "orderIndex": 6
                    },
                    {
                      "type": "SECTION",
                      "orderIndex": 7,
                      "content": [
                        {
                          "type": "HEADING",
                          "level": 3,
                          "value": "Nested Subsection",
                          "orderIndex": 8
                        },
                        {
                          "type": "PARAGRAPH",
                          "value": "This is content inside the nested subsection.",
                          "orderIndex": 9
                        }
                      ]
                    }
                  ]
                },
                {
                  "type": "TABLE",
                  "columns": ["Column 1", "Column 2"],
                  "rows": [
                    ["Row 1 Col 1", "Row 1 Col 2"],
                    ["Row 2 Col 1", "Row 2 Col 2"]
                  ],
                  "orderIndex": 10
                },
                {
                  "type": "IMAGE",
                  "value": "https://example.com/image.jpg",
                  "orderIndex": 11
                }
              ]
            }

            Ensure the extracted JSON is valid, well-structured, and follows the correct orderIndex 
            based on the original HTML structure.
            
            Parse the following HTML content: {htmlContent}
            """)
    String parseHTMLToJson(String htmlContent);
}
