FROM openjdk
ARG JAR_FILE=targe/*.jar
COPY ./target/queue-service-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]