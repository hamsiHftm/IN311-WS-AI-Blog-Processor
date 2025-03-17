package ch.hftm.xml.ws.ai.blog.processor.service;

import ch.hftm.xml.ws.ai.blog.processor.entity.*;
import ch.hftm.xml.ws.ai.blog.processor.service.model.BlogService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.StringReader;

@ApplicationScoped
public class BlogHtmlGeneratorService {

    @Inject
    BlogService blogService;

    public String generateHtml(Blog blog) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>")
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

        htmlBuilder.append("<h1>").append(blog.getTitle()).append("</h1>");

        for (Section section : blog.getSections()) {
            renderSection(section, htmlBuilder);
        }

        htmlBuilder.append("</body></html>");
        return htmlBuilder.toString();
    }

    private void renderSection(Section section, StringBuilder htmlBuilder) {
        if (section.getSectionTitle() != null && !section.getSectionTitle().isBlank()) {
            htmlBuilder.append("<h2>").append(section.getSectionTitle()).append("</h2>");
        }

        for (ContentBlock contentBlock : section.getContentBlocks()) {
            switch (contentBlock.getType()) {
                case HEADING:
                    htmlBuilder.append("<h").append(contentBlock.getLevel()).append(">")
                            .append(contentBlock.getValue())
                            .append("</h").append(contentBlock.getLevel()).append(">");
                    break;
                case PARAGRAPH:
                    htmlBuilder.append("<p>").append(contentBlock.getValue()).append("</p>");
                    break;
                case LIST:
                    renderNestedList(contentBlock, htmlBuilder);
                    break;
                case TABLE:
                    renderTable(contentBlock, htmlBuilder);
                    break;
                case IMAGE:
                    htmlBuilder.append("<img src='").append(contentBlock.getValue()).append("' alt='Image'>");
                    break;
                default:
                    break;
            }
        }

        for (Section subSection : section.getSubSections()) {
            renderSection(subSection, htmlBuilder);
        }
    }

    private void renderNestedList(ContentBlock contentBlock, StringBuilder htmlBuilder) {
        String listTag = contentBlock.getListType().equalsIgnoreCase("ordered") ? "ol" : "ul";
        htmlBuilder.append("<").append(listTag).append(">");

        JsonArray itemsArray = parseJsonArray(contentBlock.getItems());

        for (int i = 0; i < itemsArray.size(); i++) {
            JsonObject item = itemsArray.getJsonObject(i);
            htmlBuilder.append("<li>").append(item.getString("value"));

            // Check if subItems exist
            if (item.containsKey("subItems")) {
                JsonArray subItemsArray = item.getJsonArray("subItems");
                if (!subItemsArray.isEmpty()) {
                    htmlBuilder.append("<ul>");
                    for (int j = 0; j < subItemsArray.size(); j++) {
                        JsonObject subItem = subItemsArray.getJsonObject(j);
                        htmlBuilder.append("<li>").append(subItem.getString("value")).append("</li>");
                    }
                    htmlBuilder.append("</ul>");
                }
            }

            htmlBuilder.append("</li>");
        }

        htmlBuilder.append("</").append(listTag).append(">");
    }

    private void renderTable(ContentBlock contentBlock, StringBuilder htmlBuilder) {
        JsonArray columns = parseJsonArray(contentBlock.getColumns());
        JsonArray rows = parseJsonArray(contentBlock.getRows());

        htmlBuilder.append("<table border='1'><thead><tr>");
        for (int i = 0; i < columns.size(); i++) {
            htmlBuilder.append("<th>").append(columns.getString(i)).append("</th>");
        }
        htmlBuilder.append("</tr></thead><tbody>");

        for (int i = 0; i < rows.size(); i++) {
            JsonArray row = rows.getJsonArray(i);
            htmlBuilder.append("<tr>");
            for (int j = 0; j < row.size(); j++) {
                htmlBuilder.append("<td>").append(row.getString(j)).append("</td>");
            }
            htmlBuilder.append("</tr>");
        }

        htmlBuilder.append("</tbody></table>");
    }

    private JsonArray parseJsonArray(String jsonArrayString) {
        if (jsonArrayString == null || jsonArrayString.isBlank()) {
            return Json.createArrayBuilder().build();
        }

        try (JsonReader reader = Json.createReader(new StringReader(jsonArrayString))) {
            return reader.readArray();
        }
    }
}
