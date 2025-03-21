# Getting Started: AI Blog Processor

This guide helps you set up and run the AI Blog Processor project from scratch.

---

## 1. Clone the Project

```bash
git clone https://github.com/hamsiHftm/IN311-WS-AI-Blog-Processor.git
cd IN311-WS-AI-Blog-Processor
```

---

## 2. Configure the Environment

Duplicate the `.env_template` file and rename it to `.env`:

```bash
cp .env_template .env
```

Open the `.env` file and set your OpenAI API key:

```env
OPEN_AI_API_KEY=sk-your-key
```

---

## 3. Start Kafka (Bitnami Kafka Container)

Use the following command to start a Kafka instance:

```bash
docker run -d \
  --name kafka \
  --restart always \
  -p 9092:9092 \
  -e KAFKA_CFG_NODE_ID=1 \
  -e KAFKA_CFG_PROCESS_ROLES=controller,broker \
  -e KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
  -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 \
  -e KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_CFG_INTER_BROKER_LISTENER_NAME=PLAINTEXT \
  bitnami/kafka:latest
```

---

## 4. Start the Quarkus Project

```bash
./mvnw quarkus:dev
```

---

## 5. Open Swagger UI

Navigate to:  
[http://localhost:8080/q/dev-ui/io.quarkus.quarkus-smallrye-openapi/swagger-ui](http://localhost:8080/q/dev-ui/io.quarkus.quarkus-smallrye-openapi/swagger-ui)

---

## 6. Upload a Blog HTML File

Prepare a sample HTML file with blog content.  
Copy its **absolute path**.

Execute the endpoint:

**POST** `/files/upload`

With parameters:
- `fileType`: `html`
- `path`: absolute file path to your HTML file

✅ If successful, you'll receive a JSON response with a `recordId` and status `UPLOADED`.

---

## 7. Generate JSON from HTML (Asynchronous AI Processing)

Execute:

**POST** `/files/generate/json/{fileProcessingRecordId}`

Replace `{fileProcessingRecordId}` with the noted ID.  
Wait until status becomes `PROCESSED`.

---

## 8. Parse JSON and Store in DB

Execute:

**POST** `/files/parse/json/{fileProcessingRecordId}`

Replace `{fileProcessingRecordId}` with the same ID.  
Wait until status becomes `STORED`.

---

## 9. Check Processing Status

Execute:

**GET** `/files/status/{fileProcessingRecordId}`

This returns the current processing status of the file.

---

## 10. Preview Stored Blog in HTML Format

Open in browser:

```
http://localhost:8080/blog-preview/{fileProcessingRecordId}
```

Replace `{fileProcessingRecordId}` with your noted ID.  
✅ If HTML renders correctly, the workflow completed successfully.

---

## 🔧 Troubleshooting Tips

### Kafka Topic Missing?

Check if the Kafka topic was created:

```bash
docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092
```

If the topic `processing-requests` is **not listed**, restart Kafka and the Quarkus project.

---

That's it! You're good to go 🚀
