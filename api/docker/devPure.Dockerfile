FROM python:3.14-slim

WORKDIR /app

COPY ./requirements.txt /app/requirements.txt

RUN pip install --upgrade pip && pip install --no-cache-dir -r /app/requirements.txt

# Download some models
RUN python -c "from transformers import DPRQuestionEncoder, DPRContextEncoder, DPRQuestionEncoderTokenizer; \
    from transformers.utils import logging; \
    logging.set_verbosity_debug(); \
    DPRQuestionEncoder.from_pretrained('facebook/dpr-question_encoder-single-nq-base'); \
    DPRContextEncoder.from_pretrained('facebook/dpr-ctx_encoder-single-nq-base'); \
    DPRQuestionEncoderTokenizer.from_pretrained('facebook/dpr-question_encoder-single-nq-base')" \

COPY . /app

EXPOSE 9000

CMD ["uvicorn", "api.entry:create_api", "--host", "0.0.0.0", "--port", "9000", "--reload"]
