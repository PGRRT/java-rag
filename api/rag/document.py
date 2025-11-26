

class Document:
    def __init__(self, text: str, metadata: dict | None = None):
        self.text = text
        self.metadata = metadata or {}


class DocumentLoaderFactory:
    @staticmethod
    def load(path: str) -> Document:
        ext = path.lower().split(".")[-1]

        if ext == "pdf":
            pass

        if ext == "txt":
            return TXTLoader.load(path)

        raise ValueError(f"Unsupported document type: {ext}")


class TXTLoader:
    @staticmethod
    def load(path: str) -> Document:
        with open(path, "r", encoding="utf-8") as f:
            text = f.read()
        return Document(text, {"source": path, "type": "txt"})