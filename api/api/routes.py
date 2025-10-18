from typing import List, Annotated
from typing_extensions import TypedDict
from fastapi import APIRouter, UploadFile, File
from fastapi.responses import JSONResponse
from pydantic import BaseModel


import logging

logger = logging.getLogger(__file__)
logger.setLevel(logging.DEBUG)

rag_router = APIRouter()


def dummy_rag_response(conversation_id: int, message: str) -> tuple[str, List[str]]:
    """
    Returns example rag response.
    To be used before rag development is complete.
    """

    return (
        f"Example RAG Response for conversation: {conversation_id} with query: {message}",
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
    message: str


@rag_router.post("/query/{conversation_id}")
async def query(conversation_id: int, params: QueryParams) -> JSONResponse:
    """
    Query the RAG system.
    """

    message = params.message

    logger.debug(f"/query/ Received message: {message}")

    response: RagResponse = {"success": True, "message": "", "contexts": None}

    # TODO :: Error handling when rag will be ready

    try:
        message, contexts = dummy_rag_response(conversation_id, message)

        response["message"] = message
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
    logger.debug(
        f"Received {len(files)} files for conversation {conversation_id}.\nFile data {files}"
    )

    response: UploadResponse = {"success": True, "message": "Files uploaded"}

    if not files:
        response["success"] = False
        response["message"] = "No files provided"

        return JSONResponse(content=response, status_code=400)

    for file in files:
        logger.debug(
            f"Processing file: {file.filename} for conversation {conversation_id}"
        )

    return JSONResponse(content=response)


class DeleteResponse(TypedDict):
    message: str


@rag_router.delete("/delete/{converastion_id}")
async def delet_conversation(converastion_id: int) -> DeleteResponse:
    """
    Delete all conversation data.
    """
    logger.debug(f"Deleting conversation {converastion_id} data")
    return {"message": "Collection deleted"}
