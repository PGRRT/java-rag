# Medical Chatbot

This document outlines the current state of the medical chatbot project.

## Tech Stack

### Frontend

- **Framework:** React
- **Language:** TypeScript
- **Build Tool:** Vite
- **Styling:** Mantine
- **State Management:** Redux
- **HTTP Client:** Axios
- **Internationalization:** i18next

### Backend

- **Language:** Java
- **Framework:** Spring Boot
- **Architecture:** Microservices
- **Service Discovery:** Eureka
- **API Gateway:** Spring Cloud Gateway
- **Services:**
    - `chat-service`: Handles chat functionality.
    - `user-service`: Manages user-related operations.
- **Database:** PostgreSQL

### API (RAG Pipeline)

- **Language:** Python
- **Framework:** FastAPI (assumed from `entry.py` and common usage with Python APIs)
- **Core Libraries:**
    - `openai`: For interacting with the OpenAI API.
    - `pymilvus`: For connecting to the Milvus vector database.
    - `transformers`: For using Transformer models.
    - `sentence-transformers`: For generating text embeddings.
- **Functionality:** Implements a Retrieval-Augmented Generation (RAG) pipeline for question answering.
