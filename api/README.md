# Installation

### Milvus (Vector database)
```bash
sudo docker compose up -d
```
Milvus WebUI URL: http://127.0.0.1:9091/webui/

### Python
**IMPORTANT** Uncomment PyTorch CUDA in the requirements.txt file if you want to use it instead of PyTorch CPU. Currently
only PyTorch CUDA version can be used due to encoder (xformers require Nvidia GPU).
```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

# RAG Informations

Polish Information Retrieval Benchmark: https://huggingface.co/spaces/sdadas/pirb

Model used for encoder: https://huggingface.co/sdadas/stella-pl-retrieval-mini-8k
