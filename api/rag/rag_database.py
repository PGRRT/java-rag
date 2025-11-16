from typing import Any
from pymilvus import MilvusClient, CollectionSchema, DataType
from pymilvus.milvus_client import IndexParams

from uuid import UUID


class RAGDatabase:
    def __init__(self, embedding_dim: int = 2):
        self.client: MilvusClient = MilvusClient("./milvus_database.db")
        self.embedding_dim: int = embedding_dim

    def __create_collection(self, conversation_d: UUID) -> None:
        """
        This function creates a collection in the vector database. If the collection already exists, it removes it first.

        :param conversation_d: ID of the conversation
        :return: None
        """

        collection_name = self.__get_collection_name_by_id(conversation_d)

        if self.client.has_collection(collection_name):
            return

        self.client.create_collection(
            collection_name,
            self.embedding_dim,
            schema=self.__create_schema(self.embedding_dim),
        )
        self.client.create_index(collection_name, index_params=self.__create_index())

    def __get_collection_name_by_id(self, conversation_id: UUID) -> str:
        """
        This function generates a collection name based on the conversation ID.

        :param conversation_id: ID of the conversation
        :return: Collection name
        """

        return f"conversation_{conversation_id}"

    def __create_schema(self, dimension: int) -> CollectionSchema:
        """
        This function creates a schema for the collection.

        :param dimension: Dimension of the embedding
        :return: Collection schema
        """

        schema = MilvusClient.create_schema()

        # Add fields to the schema
        schema.add_field("id", datatype=DataType.INT64, is_primary=True, auto_id=True)
        schema.add_field("embedding", datatype=DataType.FLOAT_VECTOR, dim=dimension)
        schema.add_field("text", datatype=DataType.VARCHAR, max_length=65535)

        return schema

    def __create_index(self) -> IndexParams:
        """
        This function creates an index for the embedding field in the collection.

        :return: Index parameters for the embedding field
        """

        # Create an index for the embedding field
        index_params = self.client.prepare_index_params()
        index_params.add_index(
            field_name="embedding",
            index_type="FLAT",  # or "HNSW"
            metric_type="COSINE",
        )

        return index_params

    def remove_collection(self, conversation_id: UUID) -> None:
        """
        This function removes a collection from the vector database.

        :param conversation_id: ID of the conversation
        :return: None
        """

        collection_name = self.__get_collection_name_by_id(conversation_id)

        if self.client.has_collection(collection_name):
            self.client.drop_collection(collection_name)
        else:
            return

    def insert_data(self, conversation_id: UUID, data: list[Any]) -> None:
        """
        This function inserts data into the vector database. Collection will be created if it doesn't exist (for conversation_id).

        :param conversation_id: ID of the conversation
        :param data: Data to be inserted (list of dictionaries)
        example: [{"embedding": [1, 2], "text": "asap"}]
        """

        collection_name = self.__get_collection_name_by_id(conversation_id)
        self.__create_collection(conversation_d=conversation_id)
        self.client.insert(collection_name, data=data)
        self.client.flush(collection_name)

    def search(
        self, conversation_id: UUID, query_embedding: list[int]
    ) -> list[list[dict[Any, Any]]]:
        """
        This function searches for similar data in the vector database.

        :param conversation_id: ID of the conversation
        :param query_embedding: Query embedding to search for (list of lists)
        example: [[1, 2, 3], [4, 5, 6]]
        :return: Search results
        """

        collection_name = self.__get_collection_name_by_id(conversation_id)

        if not self.client.has_collection(collection_name):
            return []

        self.client.load_collection(collection_name)
        search_params = {
            "metric_type": "COSINE",
        }

        try:
            results = self.client.search(
                collection_name,
                anns_field="embedding",
                data=query_embedding,
                search_params=search_params,
                limit=5,
                output_fields=["text"],
            )
        finally:
            self.client.release_collection(collection_name)

        return results  # type: ignore
