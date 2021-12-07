docker stop mockmock
docker rm mockmock
docker build --tag maierjeanrenaud/apil4 .
docker run --name mockmock -p 8282:8282 -p 25:25 maierjeanrenaud/apil4