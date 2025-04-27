# Etapa 1: Build
FROM eclipse-temurin:24-jdk AS build

WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:24-jdk-alpine
VOLUME /tmp
WORKDIR /app

# Copia o JAR gerado
COPY --from=build /app/target/*.jar app.jar

# Porta padrão do Spring Boot
EXPOSE 8080

# Executa a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
