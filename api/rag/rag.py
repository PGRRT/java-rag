from typing_extensions import override
from abc import ABC, abstractmethod
from uuid import UUID
from sentence_transformers import SentenceTransformer
from dotenv import load_dotenv
import os
import torch.nn
from sentence_transformers import CrossEncoder

from .document import Document, DocumentLoaderFactory
from .vector_database import VectorDatabase
from .llm_client import LLM
from .llm_client import BielikLLM

class RAG(ABC):
    @abstractmethod
    def process_document(self, document: Document, conversation_id: UUID) -> None:
        pass

    @abstractmethod
    def process_query(self, query: str, conversation_id: UUID) -> str:
        pass


class MockRAG(RAG):
    @override
    def process_document(self, document: Document, conversation_id: UUID) -> None:
        pass

    @override
    def process_query(self, query: str, conversation_id: UUID) -> str:
        return "Mock process query"


class ClassicRAG(RAG):
    def __init__(self, llm: LLM):
        self.client = VectorDatabase(embedding_dim=1024)
        self.llm = llm
        print("Loading encoder...")
        self.encoder = SentenceTransformer(
    "sdadas/stella-pl-retrieval-mini-8k",
                    trust_remote_code=True,
                    device="cuda",
                    
        )
        print("Encoder loaded!")
        # self.encoder.bfloat16()
        self.encoder.half()

        print("Loading crossencoder...")
        self.crossencoder = CrossEncoder(
            "sdadas/polish-reranker-roberta-v3",
            activation_fn=torch.nn.Identity(),
            max_length=8192,
            device="cuda",
            trust_remote_code=True,
            # model_kwargs={"dtype": torch.bfloat16}
            model_kwargs={"dtype": torch.float16}
        )
        print("Crossencoder loaded!")
        self.chunk_size = 67


    @override
    def process_document(self, document: Document, conversation_id: UUID) -> None:
        document_content = document.text
        chunks_with_embeddings = self.__get_chunks_with_embeddings(document_content)

        self.client.insert_data(conversation_id, chunks_with_embeddings)


    def __text_splitter(self, text:str):
        words = text.split()
        chunks = []

        for i in range(0, len(words), self.chunk_size):
            chunk = " ".join(words[i:i + self.chunk_size])
            chunks.append(chunk)

        return chunks


    def __get_chunks_embeddings(self, chunks, batch_size=8):
        embeddings = self.encoder.encode(
            sentences=chunks,
            batch_size=batch_size,
            convert_to_tensor=True,
            show_progress_bar=True,
            normalize_embeddings=True
        )

        return embeddings


    def __get_chunks_with_embeddings(self, text, batch_size=8):
        chunks = self.__text_splitter(text)
        embeddings = self.__get_chunks_embeddings(chunks, batch_size=batch_size)

        result = [{"embedding": emb.tolist(), "text": chunk} for emb, chunk in zip(embeddings, chunks)]

        return result


    @override
    def process_query(self, query: str, conversation_id: UUID = 0) -> str:
        query_embedding = self.__get_query_embedding(query)
        contexts = self.client.search(conversation_id, query_embedding)
        reranked_contexts = self.__rerank(contexts, query)
        prompt = self.__create_prompt(query, reranked_contexts)
        print(prompt)
        response = self.llm.generate_response(prompt)
        torch.cuda.empty_cache()

        return response

    def process_query_evaluate(self, query: str, conversation_id: UUID = 0) -> dict:
        query_embedding = self.__get_query_embedding(query)
        contexts = self.client.search(conversation_id, query_embedding)
        reranked_contexts = self.__rerank(contexts, query)
        prompt = self.__create_prompt(query, reranked_contexts)
        print(prompt)
        response = self.llm.generate_response(prompt)
        torch.cuda.empty_cache()

        return {"response": response, "contexts": reranked_contexts}


    def __get_query_embedding(self, query: str):
        embedding = self.encoder.encode(
            sentences=query,
            convert_to_tensor=True,
            show_progress_bar=True,
            normalize_embeddings=True
        )

        return [embedding.tolist()] # Milvus require list[list]


    def __rerank(self, contexts: list[str], query: str, top_k: int = 5) -> list[str]:
        with torch.no_grad():
            results = self.crossencoder.predict([[query, answer] for answer in contexts])
        reranked = [
            ctx for ctx, _ in sorted(
                zip(contexts, results),
                key=lambda x: x[1],
                reverse=True
            )
        ]

        return reranked[:top_k]


    def __create_prompt(self, query: str, contexts: list):
        for i in range(len(contexts)):
            contexts[i] = f"\n<zrodlo>{contexts[i]}</zrodlo>"
        contexts = "".join(contexts)

        return f"""Pytanie użytkownika: "{query}"\nŹródła wymienione przez użytkownika: "{contexts}"\n"""


if __name__ == "__main__":
    load_dotenv()

    bielik = BielikLLM(
        api_url=os.getenv("PG_API_URL") or "",
        username=os.getenv("PG_API_USERNAME") or "",
        password=os.getenv("PG_API_PASSWORD") or "",
    )

    rag = ClassicRAG(bielik)

    document = DocumentLoaderFactory.load("../requirements.txt")
    rag.process_document(document, conversation_id=1234)

    response = rag.process_query("PyTorch", 1234)
    print(response)
    rag.client.remove_collection(1234)
