# Polish Medical Chatbot (RAG Architecture)
### Gdansk University of Technology (WETI PG)

A high-performance, distributed conversational agent designed to provide medical information based on a verified knowledge base using **Retrieval-Augmented Generation (RAG)**. The system leverages **Java 25 Virtual Threads** for scalable concurrency and an event-driven microservices architecture.

> [!IMPORTANT]
> This project requires **JDK 25** to utilize **Project Loom (Virtual Threads)**. Ensure your environment is configured for the latest Java features before building.

---

## Key Features
* **Asynchronous RAG Engine**: Semantic processing of medical queries without blocking core business logic.
* **Real-time Streaming (SSE)**: Delivers "token-by-token" responses to the frontend using Server-Sent Events for a live typing effect.
* **Virtual Thread Scaling**: Efficiently manages thousands of concurrent SSE connections with minimal RAM overhead.
* **Horizontal State Synchronization**: Uses **RabbitMQ Topic Exchanges** to route AI responses back to the specific backend instance holding the user's connection.
* **Advanced Data Persistence**: Dual-layer storage using **PostgreSQL** for relational data and **Milvus** for high-dimensional vector search.

---

## Technology Stack

### Backend & AI
* **Java 25** & **Spring Boot 3.5.7**: Core orchestrator and microservices framework.
* **FastAPI 0.120.2**: Python-based AI service for RAG logic.
* **gRPC**: High-speed internal communication between services.
* **Bielik LLM / OpenAI SDK**: Natural language generation.
* **Sentence-Transformers**: Generating 1024-dimensional semantic embeddings.

### Infrastructure & Storage
* **Milvus 2.6.5**: Vector database for similarity search.
* **PostgreSQL 17.0**: Relational database for users, chats, and messages.
* **RabbitMQ 4.2**: Message broker for event distribution.
* **Redis 8.0-alpine**: Middleware for Bloom Filters, Rate Limiting, and JWT blacklisting.
* **MinIO & etcd**: Essential support services for the Milvus standalone cluster.

### Frontend
* **React 19.2.3** & **TypeScript**: SPA framework.
* **Redux Toolkit**: Global state management.
* **Mantine UI 8.3.5**: Modern UI component library.

---

## System Architecture

### Message Flow Logic

1. **User Message (Entry Point)**
   - The user sends a request to `POST /api/v1/chats/{chatId}/messages`.
   - **Persistence**: The Chat Service saves the message to **PostgreSQL**.
   - **Real-time Update**: The service immediately emits an **SSE (Server-Sent Event)** to all users in the chat room for instant feedback.

2. **Asynchronous Delegation**
   - The Chat Service publishes a `AiRequestEvent` to **RabbitMQ**.

3. **Data Aggregation (AI Service - Java)**
   - The **Java AI Service** consumes the event.
   - It fetches additional context (e.g., the last 10 messages) using **gRPC** calls to the Chat Service.
   - It aggregates the user prompt with the retrieved history and medical context.

4. **AI Processing (Python API)**
   - The **Java AI Service** calls the **Python AI API** via `WebClient` (REST).
   - The Python module performs the RAG process (Retrieval from **Milvus** + Generation).

5. **Response Return Path**
   - The Python API returns the answer to the **Java AI Service**.
   - The Java AI Service publishes an `AiResponseEvent` to **RabbitMQ**.

6. **Final UI Update**
   - The **Chat Service** consumes the response event.
   - **Persistence**: The AI's answer is saved to **PostgreSQL**.
   - **SSE Emission**: The answer is broadcasted via **SSE** to the frontend, updating the chat UI in real-time.

### The RAG Process
1.  **Indexing**: 2,000+ medical documents are split into 67-word chunks and stored as embeddings in **Milvus**.
2.  **Retrieval**: User queries are encoded into vectors; the system retrieves the **80** most similar chunks.
3.  **Reranking**: An AI model evaluates relevance, passing the **top-5** most accurate documents to the generator.
4.  **Generation**: The LLM synthesizes a final answer based on the retrieved medical context.

---

## Setup & Deployment

> [!TIP]
> Use the provided `Makefile` to simplify Docker Compose commands.

### Prerequisites
* Docker & Docker Compose
* A valid `.env` and `.env.dev.local` file in the root directory.

### Quick Start (Development)
```bash
# Build the common-library and service images
make build-dev

# Start the entire stack (Eureka, Gateway, DBs, Services)
make start-dev