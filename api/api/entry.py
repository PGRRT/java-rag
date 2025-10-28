from fastapi import FastAPI
from starlette.datastructures import State
from enum import Enum

from rag.rag import RAG, MockRAG, ClassicRAG


class ApiMode(Enum):
    Production = 0
    Development = 1
    Testing = 2


class ApiState(State):
    rag: RAG


def create_api(mode: ApiMode = ApiMode.Development) -> FastAPI:
    """
    creates API entry point.
    """

    api = FastAPI()

    rag: RAG
    # Initializing rag
    if mode == ApiMode.Testing:
        rag = MockRAG()
    else:
        rag = ClassicRAG()

    api.state = ApiState()
    api.state.rag = rag

    from api.routes import rag_router

    api.include_router(rag_router)

    return api


if __name__ == "__main__":
    import logging

    logging.basicConfig(level=logging.DEBUG)

    # Uvicorn isn't in requirements.txt so make sure you install it before running this file

    # import uvicorn

    # uvicorn.run('api.entry:create_api',host="0.0.0.0",factory=True,port=8081,workers=4,log_level="debug",access_log=True)
