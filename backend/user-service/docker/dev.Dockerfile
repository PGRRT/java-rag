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
COPY common-library/src common-library/src
RUN ./mvnw -f common-library/pom.xml clean install -DskipTests

# Build and install user-service
COPY user-service/pom.xml user-service/pom.xml
RUN ./mvnw -f user-service/pom.xml dependency:resolve
COPY user-service/src user-service/src

# Copy entrypoint script
COPY user-service/entrypoint.sh entrypoint.sh
RUN dos2unix entrypoint.sh && chmod +x entrypoint.sh

EXPOSE 8081

ENTRYPOINT ["./entrypoint.sh"]
CMD ["./mvnw", "spring-boot:run", "-f", "user-service/pom.xml"]
