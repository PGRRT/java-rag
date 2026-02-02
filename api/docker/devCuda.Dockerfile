# FROM nvidia/cuda:13.0.2-devel-ubuntu22.04
FROM nvidia/cuda:12.2.0-base-ubuntu22.04

ENV PYTHONUNBUFFERED=1 \
  PYTHONDONTWRITEBYTECODE=1

WORKDIR /app

# Instalacja Pythona i pip
RUN apt-get update && apt-get install -y \
  python3.11 \
  python3-pip \
  python3.11-dev \
  build-essential \
  curl \
  && rm -rf /var/lib/apt/lists/*

RUN update-alternatives --install /usr/bin/python3 python3 /usr/bin/python3.11 1 \
  && ln -s /usr/bin/python3 /usr/bin/python


COPY ./requirements.txt /app/requirements.txt

# Instalacja z Twoim linkiem cu130
# RUN pip3 install --no-cache-dir --upgrade pip && \
  # pip3 install --no-cache-dir torch torchvision xformers --extra-index-url https://download.pytorch.org/whl/cu130 

RUN pip3 install --no-cache-dir --upgrade pip

RUN pip3 install --no-cache-dir \
    torch torchvision xformers \
    --index-url https://download.pytorch.org/whl/cu121

RUN pip3 install --no-cache-dir -r /app/requirements.txt

COPY . /app

EXPOSE 9000

CMD ["uvicorn", "api.entry:create_api", "--host", "0.0.0.0", "--port", "9000", "--reload"]
