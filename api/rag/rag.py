from rag_database import RAGDatabase

class RAG:
    def __init__(self):
        self.client = RAGDatabase(embedding_dim=768)
        self.embedder = 