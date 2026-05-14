# ---- Build Stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

# Compila sem rodar testes (CI/CD executa testes separadamente)
COPY src ./src
RUN mvn package -DskipTests -q

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
