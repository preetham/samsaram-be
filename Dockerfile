FROM python:3.10.9-slim-bullseye
COPY . /app/samsaram
WORKDIR /app/samsaram
RUN pip install -r requirements.txt
EXPOSE 8080
ENTRYPOINT ["python", "wsgi.py"]