# Start with a base image containing Java runtime
FROM eclipse-temurin:21-jdk

#Information around who maintains the image
LABEL org.opencontainers.image.title="accounts-service"
LABEL org.opencontainers.image.description="Accounts microservice"
LABEL org.opencontainers.image.version="1.0.0"
LABEL org.opencontainers.image.authors="easybytes.com"
LABEL org.opencontainers.image.source="https://github.com/temp.com"

# Add the application's jar to the image
COPY target/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar

# execute the application
ENTRYPOINT ["java", "-jar", "accounts-0.0.1-SNAPSHOT.jar"]