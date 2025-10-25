from rag.rag_database import RAGDatabase
from rag.llm import LLM
from rag.document_parser import parse_to_markdown

from sentence_transformers import SentenceTransformer

class RAG:
    def __init__(self, chunk_size=1024):
        self.client = RAGDatabase(embedding_dim=768)
        self.llm = LLM()
        self.embedder = SentenceTransformer("all-mpnet-base-v2")
        self.chunk_size = chunk_size

    def process_document(self, document, conversation_id):
        parsed_document = parse_to_markdown(document)

        embeddings_with_text_pairs = self.__prepare_document_embeddings_with_corresponding_text(parsed_document)
        self.client.insert_data(conversation_id, embeddings_with_text_pairs)

    def __prepare_document_embeddings_with_corresponding_text(self, document: str) -> list:
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
            {
                "text": fragment,
                "embedding": embedding.tolist()
            } for fragment, embedding in zip(fragments, embeddings)
        ]

        return embeddings_with_text_pairs

    def __tokenize(self, text: str) -> list:
        """
        Tokenize the input text into fixed-size tokens.

        :param text: Input text to be tokenized
        :return: List of fixed-size tokens
        """

        # Create fixed-size tokens
        return [text[i:i + self.chunk_size] for i in range(0, len(text), self.chunk_size)]

    def process_query(self, query: str, conversation_id: int):
        relevant_documents = self.__get_relevant_documents_by_query(conversation_id, query)
        prompt = f"Pytanie: {query} \n \n {relevant_documents}"

        response = self.llm.generate_response(prompt)

        return response

    def __get_relevant_documents_by_query(self, conversation_id: int, query: str) -> list:
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

        # Create a list of relevant documents (text only)
        result = []
        for i in results[0]:
            result.append(i['entity']['text'])

        return result