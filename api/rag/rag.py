from typing import Any
from typing_extensions import override

from pymupdf import Document
from rag.rag_database import RAGDatabase
from rag.llm import LLM
from rag.document_parser import parse_to_markdown
from abc import ABC, abstractmethod

from sentence_transformers import SentenceTransformer


class RAG(ABC):
    @abstractmethod
    def process_document(self, document: Document, conversation_id: int) -> None:
        pass

    @abstractmethod
    def process_query(self, query: str, conversation_id: int) -> str:
        pass


class MockRAG(RAG):
    @override
    def process_document(self, document: Document, conversation_id: int) -> None:
        pass

    @override
    def process_query(self, query: str, conversation_id: int) -> str:
        return "Mock process query"


class ClassicRAG(RAG):
    def __init__(self, llm: LLM, chunk_size: int = 1024):
        self.client: RAGDatabase = RAGDatabase(embedding_dim=768)
        self.llm: LLM = llm
        self.embedder: SentenceTransformer = SentenceTransformer("all-mpnet-base-v2")
        self.chunk_size: int = chunk_size

    @override
    def process_document(self, document: Document, conversation_id: int) -> None:
        parsed_document = parse_to_markdown(document)

        embeddings_with_text_pairs = (
            self.__prepare_document_embeddings_with_corresponding_text(parsed_document)
        )
        self.client.insert_data(conversation_id, embeddings_with_text_pairs)

    @override
    def process_query(self, query: str, conversation_id: int) -> str:
        relevant_documents = self.__get_relevant_documents_by_query(
            conversation_id, query
        )
        prompt = f"Pytanie: {query} \n \n {relevant_documents}"

        response = self.llm.generate_response(prompt)

        return response

    def __prepare_document_embeddings_with_corresponding_text(
        self, document: str
    ) -> list[dict[Any, Any]]:
        """
        Prepare document embeddings by splitting the document into fragments and vectorizing them.

        :param document: Document to be embedded
        :return: List of document fragments
        """

        # Split the document into fragments
        fragments = self.__tokenize(document)

        # Vectorize the fragments
        embeddings = self.embedder.encode(fragments, show_progress_bar=True)

        # Create a list of dictionaries with text and embedding
        embeddings_with_text_pairs = [
            {"text": fragment, "embedding": embedding.tolist()}
            for fragment, embedding in zip(fragments, embeddings)
        ]

        return embeddings_with_text_pairs

    def __tokenize(self, text: str) -> list[str]:
        """
        Tokenize the input text into fixed-size tokens.

        :param text: Input text to be tokenized
        :return: List of fixed-size tokens
        """

        # Create fixed-size tokens
        return [
            text[i : i + self.chunk_size] for i in range(0, len(text), self.chunk_size)
        ]

    def __get_relevant_documents_by_query(
        self, conversation_id: int, query: str
    ) -> list[str]:
        """
        Get relevant documents by processing the query and searching the vector database.

        :param conversation_id: ID of the conversation
        :param query: Query to be processed
        :return: List of relevant documents
        """

        # Embedding
        query_embedding = self.embedder.encode([query], show_progress_bar=True)

        # Search the vector database
        results = self.client.search(conversation_id, query_embedding.tolist())

        if not results:
            return []

        # Create a list of relevant documents (text only)
        result = []
        for i in results[0]:
            result.append(i["entity"]["text"])

        return result
