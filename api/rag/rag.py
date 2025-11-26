import uuid
from typing import Any
from typing_extensions import override
from abc import ABC, abstractmethod
from uuid import UUID
from transformers import DPRQuestionEncoder, DPRContextEncoder, DPRQuestionEncoderTokenizer
from transformers.utils import logging
from dotenv import load_dotenv
import os
import time
import torch
import torch.nn.functional as F
from tqdm import tqdm

from document import Document, DocumentLoaderFactory
from vector_database import VectorDatabase
from llm_client import LLM
from llm_client import BielikLLM


logging.set_verbosity_debug()


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
        self.client = VectorDatabase(embedding_dim=768)
        self.llm = llm
        self.question_encoder = DPRQuestionEncoder.from_pretrained("facebook/dpr-question_encoder-single-nq-base")
        self.context_encoder = DPRContextEncoder.from_pretrained("facebook/dpr-ctx_encoder-single-nq-base")
        self.tokenizer = DPRQuestionEncoderTokenizer.from_pretrained("facebook/dpr-question_encoder-single-nq-base")
        self.chunk_size = 100

        self.question_encoder.eval() # Evaluation mode on
        self.context_encoder.eval() # Evaluation mode on


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


    def __get_chunks_embeddings(self, chunks, batch_size=16):
        embeddings = []

        for i in tqdm(range(0, len(chunks), batch_size), desc="Generating embeddings"):
            batch = chunks[i:i + batch_size]

            inputs = self.tokenizer(
                batch,
                return_tensors="pt",
                padding=True,
            )

            with torch.no_grad():
                emb = self.context_encoder(**inputs).pooler_output  # [B, 768]

            emb = F.normalize(emb, p=2, dim=1)

            embeddings.append(emb)

        return torch.cat(embeddings, dim=0)


    def __get_chunks_with_embeddings(self, text, batch_size=16):
        chunks = self.__text_splitter(text)
        embeddings = self.__get_chunks_embeddings(chunks, batch_size=batch_size)

        result = [{"embedding": emb.tolist(), "text": chunk} for emb, chunk in zip(embeddings, chunks)]

        return result


    @override
    def process_query(self, query: str, conversation_id: UUID) -> str:
        base_conversation_id = 1234 # Nwm jakis cwel na frontendzie wymyslil sobie chaty, mimo ze tego nie potrzebujemy teraz

        query_embedding = self.__get_query_embedding(query)
        contexts = self.client.search(base_conversation_id, query_embedding)
        prompt = self.__create_prompt(query, contexts)
        response = self.llm.generate_response(prompt)

        return response


    def __get_query_embedding(self, query: str):
        inputs = self.tokenizer(
                query,
                return_tensors="pt",
                padding=True,
                truncation=True,
        )

        with torch.no_grad():
            emb = self.question_encoder(**inputs).pooler_output

        emb = F.normalize(emb, p=2, dim=1)

        return emb.tolist()


    def __create_prompt(self, query: str, contexts: list):
        contexts = "\n".join(contexts)

        return f"""Pytanie użytkownika: "{query}"\nŹródła wymienione przez użytkownika: "{contexts}"\n"""


if __name__ == "__main__":
    load_dotenv()

    bielik = BielikLLM(
        api_url=os.getenv("PG_API_URL") or "",
        username=os.getenv("PG_API_USERNAME") or "",
        password=os.getenv("PG_API_PASSWORD") or "",
    )

    rag = ClassicRAG(bielik)

    print(rag.process_query("Ile lat ma 5 letni pies?", 1234))