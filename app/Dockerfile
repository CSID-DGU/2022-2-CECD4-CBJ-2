FROM python:3.8-slim-buster

COPY . /app
WORKDIR /app/

RUN pip install -r requirements.txt

ENV PYTHONPATH "${PYTHONPATH}:/app"
ENV PYTHONUNBUFFERED 1

CMD uvicorn --host=0.0.0.0 --port 8000 main:app