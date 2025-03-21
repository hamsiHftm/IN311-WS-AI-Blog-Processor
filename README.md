# AI Blog Processor (LangChain4j + Quarkus)

This project extends a blog-processing architecture by integrating AI-powered content structuring using [LangChain4j](https://github.com/langchain4j/langchain4j) within a modern Quarkus application.

Users can upload an HTML file containing blog content, and the system will process it using an AI service, convert it to structured JSON, save the file under `/data/output/json/{fileProcessingRecordID}.json` in the root directory of the application, and persist the **absolute path** of the file in the PostgreSQL database. The processed blog can then be rendered as a styled HTML preview, implemented primarily for testing and demonstration purposes.

## Features

- HTML File Upload via REST endpoint
- AI-Powered Structuring using LangChain4j + OpenAI
- JSON Output with blog sections and content blocks
- PostgreSQL Storage using Panache ORM
- Kafka Integration for asynchronous file processing
- REST API for interaction and previews
- Modular architecture with DTOs, services, and repositories
- Docker-ready
- .env template for OpenAI credentials

## Tech Stack

| Layer        | Technology                      |
|--------------|----------------------------------|
| Backend      | Quarkus (REST, CDI, Panache ORM) |
| AI           | LangChain4j + OpenAI             |
| Messaging    | Apache Kafka (SmallRye)          |
| Persistence  | PostgreSQL                       |
| JSON Parsing | Jakarta JSON                     |
| Build Tool   | Maven                            |
| Deployment   | Docker                           |

## Asynchronous Workflow with Kafka

The system uses **Kafka** to decouple file processing from user-facing API calls. When a file is uploaded and JSON generation is triggered, the system sends a message to a Kafka topic. A background worker (annotated with `@Incoming`) listens for processing requests.

The processing is handled using **Mutiny's `Uni`** to ensure that threads are **non-blocking** and efficiently scheduled:

- This means the client can **trigger further steps (e.g. JSON parsing or DB storage) at any time**, even if processing is still in progress.

## Workflow Overview

```
+--------------------------+
|  User Uploads HTML File  |
+------------+-------------+
             |
             v
+------------------------------+
| Validate & Save File Info   |
| (absolute path + status)    |
+------------+----------------+
             |
             v
+------------------------------+
| User starts JSON Generation |
|    (Triggers Kafka Event)   |
+------------+----------------+
             |
             v
+--------------------------------------+
| Kafka Topic: file-processing-requests|
+----------------+---------------------+
                 |
                 v
+--------------------------------+
| Worker processes file with AI |
| (HTML -> JSON via LangChain4j)|
+----------------+---------------+
                 |
                 v
+----------------------------------------------+
| Save JSON to /data/output/json/{id}.json     |
| Save absolute path to DB                     |
+----------------+-----------------------------+
                 |
                 v
+-------------------------------+
| User triggers DB storage      |
| (Parse & Save Blog + Sections)|
+----------------+--------------+
                 |
                 v
+-------------------------------+
| Preview Blog via HTML Page    |
| /blog-preview/{blogId}        |
+-------------------------------+
```

## REST API – Endpoints & Examples

### 1. Upload HTML File

**POST** `/files/upload?path={absolutePath}&fileType=html`

- `path`: **Absolute path required**. The server checks if the file exists before proceeding.
- `fileType`: Must be `"html"` (PDF is scaffolded but **not implemented**).

Example:
```
POST http://localhost:8080/files/upload?path=/home/user/test.html&fileType=html
```

### 2. Generate JSON (asynchronous)

**POST** `/files/generate/json/{fileProcessingRecordId}`

- The process only be started if the file processing record status is `UPLOADED`
- Sends a Kafka event to process the file using AI
- Returns immediately — processing is handled in the background

Example:
```
POST http://localhost:8080/files/generate/json/1
```

### 3. Check Processing Status

**GET** `/files/status/{fileProcessingRecordId}`

Example:
```
GET http://localhost:8080/files/status/1
```

Returns status: UPLOADING, UPLOADED, PROCESSING, PROCESSED, STORED, or FAILED

### 4. Parse JSON and Store in DB

**POST** `/files/parse/json/{fileProcessingRecordId}`

- Converts the previously generated JSON into database entries
- Only works if status is `PROCESSED`

Example:
```
POST http://localhost:8080/files/parse/json/1
```

### 5. Preview Final Blog HTML

**GET** `/blog-preview/{blogId}`

Example:
```
GET http://localhost:8080/blog-preview/2
```

Returns blog content rendered in HTML.

## JSON Output Structure

The full JSON schema expected and processed by the system is documented in:

**[README_Json_Structure.md](README_Json_Structure.md)**

## Class Diagram

A class diagram of the entities (Blog, Section, ContentBlock, etc.) will be added here soon for a clear visual understanding of the database structure.

## How to Start & Test

A separate README will follow that covers:

1. Cloning the project
2. Setting up the environment (.env for OpenAI)
3. Starting the app with Docker or dev mode
4. Using sample test files for processing

**[README_DEV_MODE_START.md](README_DEV_MODE_START.md)**

## Limitations & Notes

- PDF processing support is not implemented (scaffolded only)
- No integration/unit tests included
- Swagger/OpenAPI documentation is not added
- File paths must be absolute and accessible from the server
- Assumes local dev/testing setup

## Acknowledgements

- [LangChain4j](https://github.com/langchain4j/langchain4j)
- [Quarkus](https://quarkus.io/)
- [OpenAI](https://platform.openai.com/)
- [SmallRye Kafka](https://smallrye.io/smallrye-reactive-messaging)

## Repository

https://github.com/hamsiHftm/IN311-WS-AI-Blog-Processor

## Author

Hamsiga Rajaratnam (`hamsiHftm`)
