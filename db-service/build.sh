docker build -t h2-db .
docker create -p 8080:8082 --name db h2-db

