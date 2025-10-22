from typing import List, Annotated
from typing_extensions import TypedDict
from fastapi import APIRouter, UploadFile, File
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from typing import Optional


import logging

logger = logging.getLogger("routes")
logger.setLevel(logging.DEBUG)

console_handler = logging.StreamHandler()
console_handler.setLevel(logging.DEBUG)

formatter = logging.Formatter("%(levelname)s | %(name)s | %(message)s")
console_handler.setFormatter(formatter)

# Attach the handler to the logger
logger.addHandler(console_handler)

# Disable propagation if you don't want library logs
logger.propagate = False

rag_router = APIRouter()


def dummy_rag_response(
    conversation_id: int, query: str, history: List[str]
) -> tuple[str, List[str]]:
    """
    Returns example rag response.
    To be used before rag development is complete.
    """

    return (
        f"Example RAG Response for conversation: {conversation_id} with query: {query}. conversation history size: : {history}",
        [
            "Context 1",
            "Really long context number 2 Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            "Context 3 Example context number 3 from rag rag rag rag rag rag rag",
        ],
    )


class RagResponse(TypedDict):
    """
    RAG response template.
    """

    success: bool
    message: str
    contexts: List[str] | None


class QueryParams(BaseModel):
    query: str
    """User query"""

    message_history: Optional[List[str]] = None
    """List of previous messages in given conversation"""


@rag_router.post("/query/{conversation_id}")
async def query(conversation_id: int, params: QueryParams) -> JSONResponse:
    """
    Query the RAG system.
    """

    query = params.query
    history = params.message_history if params.message_history else []

    logger.debug(f"/query/ Received message: {query} with history size: {len(history)}")

    response: RagResponse = {"success": True, "message": "", "contexts": None}

    # TODO :: Error handling when rag will be ready

    try:
        rag_response, contexts = dummy_rag_response(conversation_id, query, history)

        response["message"] = rag_response
        response["contexts"] = contexts

        return JSONResponse(content=response)

    except Exception as e:
        logging.error(f"Error during generation of RAG response. {e}")

        response["message"] = "Internal server error when processing query"
        response["success"] = False

        return JSONResponse(content=response, status_code=500)


class UploadResponse(TypedDict):
    success: bool
    message: str | None


@rag_router.post("/upload/{conversation_id}")
async def upload_documents(
    conversation_id: int, files: Annotated[List[UploadFile], File(...)] = []
) -> JSONResponse:
    """
    Upload documents to RAG system.
    """
    logger.info(
        f"Received {len(files)} files for conversation {conversation_id}.\nFile data {files}"
    )

    response: UploadResponse = {"success": True, "message": "Files uploaded"}

    if not files:
        logger.debug("No files provided")
        response["success"] = False
        response["message"] = "No files provided"

        return JSONResponse(content=response, status_code=400)

    for file in files:
        logger.info(
            f"Processing file: {file.filename} for conversation {conversation_id}"
        )

    return JSONResponse(content=response, status_code=201)


class DeleteResponse(TypedDict):
    status: bool
    message: str


@rag_router.delete("/delete/{converastion_id}")
async def delete_conversation(converastion_id: int) -> DeleteResponse:
    """
    Delete all conversation data.
    """
    logger.debug(f"Deleting conversation {converastion_id} data")

    return {"status": True, "message": "Collection deleted"}
