package ch.hftm.xml.ws.ai.blog.processor.service;

import ch.hftm.xml.ws.ai.blog.processor.entity.Blog;
import ch.hftm.xml.ws.ai.blog.processor.entity.ContentBlock;
import ch.hftm.xml.ws.ai.blog.processor.entity.Section;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BlogHtmlGeneratorService {
    public String generateHtml(Blog blog) {
        StringBuilder html = new StringBuilder();

        // Start HTML Document
        html.append("<!DOCTYPE html>")
                .append("<html lang='en'>")
                .append("<head>")
                .append("<meta charset='UTF-8'>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                .append("<title>").append(blog.getTitle()).append("</title>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; max-width: 800px; margin: auto; line-height: 1.6; }")
                .append("h1 { text-align: center; }")
                .append("h2, h3, h4 { color: #333; }")
                .append("ul { padding-left: 20px; }")
                .append("p { margin-bottom: 15px; }")
                .append("img { max-width: 100%; height: auto; display: block; margin: 10px auto; }")
                .append("table { width: 100%; border-collapse: collapse; margin-bottom: 15px; }")
                .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
                .append("</style>")
                .append("</head><body>");

        // Blog Title
        html.append("<h1>").append(blog.getTitle()).append("</h1>");

        // Loop through sections
        for (Section section : blog.getSections()) {
            generateSectionHtml(html, section);
        }

        // Close HTML
        html.append("</body></html>");

        return html.toString();
    }

    private void generateSectionHtml(StringBuilder html, Section section) {
        if (section.getSectionTitle() != null && !section.getSectionTitle().isBlank()) {
            html.append("<h2>").append(section.getSectionTitle()).append("</h2>");
        }

        for (ContentBlock block : section.getContentBlocks()) {
            switch (block.getType()) {
                case HEADING:
                    html.append("<h").append(block.getLevel()).append(">")
                            .append(block.getValue())
                            .append("</h").append(block.getLevel()).append(">");
                    break;
                case PARAGRAPH:
                    html.append("<p>").append(block.getValue()).append("</p>");
                    break;
                case LIST:
                    html.append("<ul>");
                    for (String item : block.getItems().replace("[", "").replace("]", "").split(",")) {
                        html.append("<li>").append(item.trim().replace("\"", "")).append("</li>");
                    }
                    html.append("</ul>");
                    break;
                case TABLE:
                    html.append("<table><tr>");
                    for (String col : block.getColumns().replace("[", "").replace("]", "").split(",")) {
                        html.append("<th>").append(col.trim().replace("\"", "")).append("</th>");
                    }
                    html.append("</tr>");
                    for (String row : block.getRows().replace("[", "").replace("]", "").split("],")) {
                        html.append("<tr>");
                        for (String cell : row.replace("[", "").replace("]", "").split(",")) {
                            html.append("<td>").append(cell.trim().replace("\"", "")).append("</td>");
                        }
                        html.append("</tr>");
                    }
                    html.append("</table>");
                    break;
                case IMAGE:
                    html.append("<img src='").append(block.getValue()).append("' alt='Image'>");
                    break;
                default:
                    html.append("<p>[Unknown Content]</p>");
                    break;
            }
        }

        // Render nested sections
        for (Section subSection : section.getSubSections()) {
            generateSectionHtml(html, subSection);
        }
    }
}
