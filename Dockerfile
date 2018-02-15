FROM openjdk:8
ADD target/docker-client.jar docker-client.jar
ENTRYPOINT ["java","-jar","docker-client.jar"]
