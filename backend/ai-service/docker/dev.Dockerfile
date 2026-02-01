FROM eclipse-temurin:25

WORKDIR /app

RUN apt-get update && apt-get install -y bash dos2unix curl \
    && rm -rf /var/lib/apt/lists/*

# Copy and build common-library
COPY common-library/mvnw mvnw
COPY common-library/.mvn .mvn
RUN dos2unix mvnw && chmod +x mvnw

# Build and install common-library
COPY common-library/pom.xml common-library/pom.xml
RUN ./mvnw -f common-library/pom.xml clean install -DskipTests
COPY common-library/src common-library/src

# Build and install user-service
COPY ai-service/pom.xml ai-service/pom.xml
RUN ./mvnw -f ai-service/pom.xml dependency:resolve
COPY ai-service/src ai-service/src

# Copy entrypoint script
COPY ai-service/entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh

EXPOSE 8082

ENTRYPOINT ["./entrypoint.sh"]
CMD ["./mvnw", "spring-boot:run", "-f", "ai-service/pom.xml"]
