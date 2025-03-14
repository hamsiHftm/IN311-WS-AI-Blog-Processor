# 🚀 Running Kafka with Docker (Without Docker-Compose)

This guide provides step-by-step instructions on how to run **Kafka** manually using `docker run` commands without needing a `docker-compose.yml` file.

---

## ** Start Kafka**
Now start **Kafka**:

```sh
docker run -d --name kafka -p 9092:9092 \
  -e KAFKA_CFG_NODE_ID=1 \
  -e KAFKA_CFG_PROCESS_ROLES=controller,broker \
  -e KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
  -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_CFG_INTER_BROKER_LISTENER_NAME=PLAINTEXT \
  bitnami/kafka:latest
```

## **Create a Kafka Topic**
Kafka **does not auto-create topics**, so create one manually for **file-processing requests**:

```sh
docker exec -it kafka kafka-topics --create \
  --topic processing_requests \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
```

### **Explanation:**
- Creates a topic **`processing_requests`** with **3 partitions** and **replication factor 1**.

- **List Available Topics:**
```sh
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

---

## **Stop Kafka**
To **stop Kafka**:
```sh
docker stop kafka
```

To **remove Kafka & Zookeeper**:
```sh
docker rm kafka zookeeper
```

---

## **✅ Summary**
**Start Zookeeper:**
```sh
docker run -d --name zookeeper -p 2181:2181 -e ZOOKEEPER_CLIENT_PORT=2181 confluentinc/cp-zookeeper:latest
```
**Start Kafka:**
```sh
docker run -d --name kafka -p 9092:9092 --link zookeeper:zookeeper \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka:latest
```
**Create Kafka Topic:**
```sh
docker exec -it kafka kafka-topics --create --topic processing_requests --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```
**List All Topics in Kafka**
```sh
docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092
```
---

🎯 Now Kafka is ready to use! 🚀

