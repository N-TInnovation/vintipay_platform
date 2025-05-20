# Build stage
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copiar o projeto
COPY . .

# Dar permissão de execução ao mvnw
RUN chmod +x mvnw

# Expor a porta
EXPOSE 8443

# Executar a aplicação pulando os testes
ENTRYPOINT ["./mvnw", "spring-boot:run", "-DskipTests"]