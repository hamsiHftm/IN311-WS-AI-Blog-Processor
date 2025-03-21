package ch.hftm.xml.ws.ai.blog.processor.service.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.PdfUrl;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface PDFProcessingAIService {

    @SystemMessage("You are an AI-powered PDF parser that extracts structured blog content from PDF files.")

    @UserMessage("""
            Extract structured blog content from the provided PDF document and return a **valid JSON object**. 
            Ensure **headings, paragraphs, lists (including nested lists), tables, and images** are correctly structured. 
            
            **Return only the JSON output, without any additional text, explanations, or markdown.**  
            Do **not** include any preambles like 'Here is your JSON output'.  
            
            ---
            
            **Expected JSON Structure:**
            ```json
            {
              "title": "Extracted Blog Title",
              "content": [
                {
                  "type": "HEADING",
                  "level": 2,
                  "value": "Main Heading",
                  "orderIndex": 1
                },
                {
                  "type": "PARAGRAPH",
                  "value": "This is a paragraph describing the content.",
                  "orderIndex": 2
                },
                {
                  "type": "LIST",
                  "listType": "unordered",
                  "items": [
                    {
                      "value": "Main item 1",
                      "subItems": [
                        {
                          "value": "Sub-item 1.1"
                        },
                        {
                          "value": "Sub-item 1.2"
                        }
                      ]
                    },
                    {
                      "value": "Main item 2",
                      "subItems": [
                        {
                          "value": "Sub-item 2.1"
                        }
                      ]
                    }
                  ],
                  "orderIndex": 3
                },
                {
                  "type": "TABLE",
                  "columns": ["Column 1", "Column 2"],
                  "rows": [
                    ["Row 1 Col 1", "Row 1 Col 2"],
                    ["Row 2 Col 1", "Row 2 Col 2"]
                  ],
                  "orderIndex": 4
                },
                {
                  "type": "IMAGE",
                  "value": "https://example.com/image.jpg",
                  "orderIndex": 5
                }
              ]
            }
            ```
            
            ---
            
            **Now, analyze the following PDF file and return only the JSON output:**  
            {fileId}
            """)
    String extractContent(@PdfUrl String fileId);
}
