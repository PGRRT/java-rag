FROM tensorflow/tensorflow:2.20.0-gpu

WORKDIR /app

COPY ./requirements.txt /app/requirements.txt

RUN pip install --upgrade pip && pip install -r /app/requirements.txt

COPY . /app

EXPOSE 8081

CMD ["fastapi", "dev", "--entrypoint", "api.entry:create_api", "--port", "8081"]
