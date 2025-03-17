# JSON Structure for AI-Processed Blog Content

This document describes the structured JSON format used to represent blog content processed by AI. The JSON format captures headings, paragraphs, images, lists, tables, and nested sections in a structured way.

## File Location
The example JSON file is available in the `resources` folder under the name `example_blog_structure.json`.

## JSON Structure

```json
{
  "title": "Example Blog Title",
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
          "orderIndex": 7
        },
        {
          "type": "SECTION",
          "orderIndex": 8,
          "content": [
            {
              "type": "HEADING",
              "level": 3,
              "value": "Nested Subsection",
              "orderIndex": 9
            },
            {
              "type": "PARAGRAPH",
              "value": "This is content inside the nested subsection.",
              "orderIndex": 10
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
      "orderIndex": 11
    },
    {
      "type": "IMAGE",
      "value": "https://example.com/image.jpg",
      "orderIndex": 12
    }
  ]
}
```

## Explanation of Fields

- **`title`**: The blog's title.
- **`content`**: A list of content blocks containing different content types.
- **`type`**: Defines the type of content (e.g., HEADING, PARAGRAPH, IMAGE, LIST, TABLE, SECTION).
- **`level`** (for HEADINGS): Defines the heading level (1-6).
- **`value`**: The actual text content, image URL.
- **`orderIndex`**: Defines the order of content appearance.
- **`listType`** (for LISTS): Defines whether the list is "ordered" or "unordered".
- **`items`** (for LISTS): Contains list items.
- **`subitems`** (for LISTS): Contains list of nested items from list.
- **`columns`** (for TABLES): Defines table column names.
- **`rows`** (for TABLES): Contains table row data.
- **`values`** (for SECTIONS): A nested list of content items within a section.

## Nested Sections
The `SECTION` type allows hierarchical structuring of content. A section contains its own list of nested content blocks inside the `content` field.

## Usage
This JSON format ensures structured representation of blog data for efficient AI processing, database storage, and frontend rendering. The hierarchical structure supports complex blog layouts while remaining easy to parse and use.

---


