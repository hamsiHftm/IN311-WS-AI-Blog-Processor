package ch.hftm.xml.ws.ai.blog.processor.service;

import ch.hftm.xml.ws.ai.blog.processor.entity.*;
import ch.hftm.xml.ws.ai.blog.processor.service.model.BlogService;
import ch.hftm.xml.ws.ai.blog.processor.service.model.FileProcessingRecordService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class JsonProcessingService {

    private static final Logger LOG = Logger.getLogger(JsonProcessingService.class);

    @Inject
    FileService fileService;

    @Inject
    BlogService blogService;

    @Inject
    FileProcessingRecordService fileProcessingRecordService;

    // JSON Keys as Constants
    private static class JsonKeys {
        static final String TITLE = "title";
        static final String CONTENT = "content";
        static final String TYPE = "type";
        static final String VALUE = "value";
        static final String ORDER_INDEX = "orderIndex";
        static final String LEVEL = "level";
        static final String LIST_TYPE = "listType";
        static final String ITEMS = "items";
        static final String COLUMNS = "columns";
        static final String ROWS = "rows";
        public static String SUB_ITEMS = "subItems";

    }

    @Transactional
    public Blog parseAndSaveBlog(FileProcessingRecord record) throws Exception {
        JsonObject jsonObject = fileService.readJsonFile(record);

        // Extract Blog title
        String title = jsonObject.getString(JsonKeys.TITLE, "Untitled Blog");
        Blog blog = new Blog();
        blog.setTitle(title);

        // Extract sections and content
        JsonArray contentArray = jsonObject.getJsonArray(JsonKeys.CONTENT);
        if (contentArray != null) {
            for (JsonValue value : contentArray) {
                if (value.getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject contentObject = value.asJsonObject();
                    parseContent(blog, contentObject, null); // Recursive parsing
                }
            }
        }

        // Save the blog
        blogService.saveBlog(blog);

        // Update the record status
        record.setBlog(blog);
        record.setStatus(ProcessingStatus.STORED);
        fileProcessingRecordService.updateFileProcessingRecord(record);

        LOG.info("Blog successfully saved: " + title);
        return blog;
    }

    private void parseContent(Blog blog, JsonObject contentObject, Section parentSection) {
        String typeString = contentObject.getString(JsonKeys.TYPE, "");
        ContentType typeEnum = ContentType.fromString(typeString);

        if (typeEnum == null) {
            LOG.warn("Unknown content type: " + typeString);
            return;
        }

        int orderIndex = contentObject.getInt(JsonKeys.ORDER_INDEX, 0);

        switch (typeEnum) {
            case HEADING:
                ContentBlock heading = new ContentBlock();
                heading.setType(ContentType.HEADING);
                heading.setValue(contentObject.getString(JsonKeys.VALUE, ""));
                heading.setOrderIndex(orderIndex);
                heading.setLevel(contentObject.getInt(JsonKeys.LEVEL, 1));
                attachToParent(parentSection, heading, blog);
                break;

            case PARAGRAPH:
                ContentBlock paragraph = new ContentBlock();
                paragraph.setType(ContentType.PARAGRAPH);
                paragraph.setValue(contentObject.getString(JsonKeys.VALUE, ""));
                paragraph.setOrderIndex(orderIndex);
                attachToParent(parentSection, paragraph, blog);
                break;

            case LIST:
                ContentBlock list = new ContentBlock();
                list.setType(ContentType.LIST);
                list.setListType(contentObject.getString(JsonKeys.LIST_TYPE, "unordered"));
                list.setItems(contentObject.getJsonArray(JsonKeys.ITEMS).toString());

                if (contentObject.containsKey(JsonKeys.SUB_ITEMS)) {
                    list.setSubItems(contentObject.getJsonArray(JsonKeys.SUB_ITEMS).toString()); // Store as JSON string
                }

                list.setOrderIndex(orderIndex);
                attachToParent(parentSection, list, blog);
                break;

            case TABLE:
                ContentBlock table = new ContentBlock();
                table.setType(ContentType.TABLE);
                table.setColumns(contentObject.getJsonArray(JsonKeys.COLUMNS).toString());
                table.setRows(contentObject.getJsonArray(JsonKeys.ROWS).toString());
                table.setOrderIndex(orderIndex);
                attachToParent(parentSection, table, blog);
                break;

            case IMAGE:
                ContentBlock image = new ContentBlock();
                image.setType(ContentType.IMAGE);
                image.setValue(contentObject.getString(JsonKeys.VALUE, ""));
                image.setOrderIndex(orderIndex);
                attachToParent(parentSection, image, blog);
                break;

            case SECTION:
                Section section = new Section();
                section.setSectionTitle(contentObject.getString(JsonKeys.VALUE, "Untitled Section"));
                section.setBlog(blog);
                section.setParentSection(parentSection);
                blog.getSections().add(section);

                JsonArray subContentArray = contentObject.getJsonArray(JsonKeys.CONTENT);
                if (subContentArray != null) {
                    for (JsonValue subValue : subContentArray) {
                        if (subValue.getValueType() == JsonValue.ValueType.OBJECT) {
                            parseContent(blog, subValue.asJsonObject(), section);
                        }
                    }
                }
                break;
        }
    }

    private void attachToParent(Section parentSection, ContentBlock contentBlock, Blog blog) {
        if (parentSection != null) {
            contentBlock.setSection(parentSection);
            parentSection.getContentBlocks().add(contentBlock);
        } else {
            if (blog.getSections().isEmpty()) {
                Section defaultSection = new Section();
                defaultSection.setBlog(blog);
                blog.getSections().add(defaultSection);
            }
            contentBlock.setSection(blog.getSections().get(0));
            blog.getSections().get(0).getContentBlocks().add(contentBlock);
        }
    }
}
