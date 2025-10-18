from fastapi import FastAPI


def create_api() -> FastAPI:
    """
    creates API entry point.
    """

    api = FastAPI()

    from api.routes import rag_router

    api.include_router(rag_router)

    return api
