# VintiPay

Serviço de integração com o gateway de pagamento **SISP** (Cabo Verde), com suporte a recibos, pagamentos, assinaturas e integrações externas (Stripe, Vendus, Brevo, Keycloak).

## Stack

- **Java 17** + Spring Boot 3.4
- Spring MVC + Thymeleaf (frontend server-side)
- Spring Security — OAuth2 Client / Resource Server (Keycloak)
- Spring Data JPA + Flyway — MySQL (prod) / H2 (testes)
- Apache Kafka (mensageria de webhooks)
- Spring Cloud OpenFeign
- Stripe Java, Springdoc OpenAPI, OpenCSV, Apache POI, ZXing
- Maven (`mvnw`)

## Arquitetura

Arquitetura hexagonal (ports & adapters) sob o pacote `cv.ntinnovation.vintipay`:

```
adapter/in/    → controllers REST/Thymeleaf, consumers Kafka
adapter/out/   → persistência JPA, Feign clients, storage, location
application/
  port/in/     → interfaces de use case
  port/out/    → interfaces de repository/client
  core/        → domínio, use cases (UseCaseImpl), services
config/        → beans, segurança, importação de dados
```

Convenções detalhadas em `.devin/memory/conventions.md`. Decisões arquiteturais em `docs/adrs/`.

## Pré-requisitos

- Java 17
- Docker e Docker Compose
- Maven não é necessário (usar o wrapper `./mvnw`)

## Configuração

As secrets e credenciais são externalizadas via variáveis de ambiente — nunca versionadas.

```bash
cp .env.example .env                # execução local
cp .env.docker.example .env.docker  # execução via Docker Compose
```

Preencha os valores reais (Brevo, Stripe, Keycloak, Vendus, etc.) nos arquivos copiados.

## Executando

### Local (perfil `local`)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Requer MySQL e Kafka acessíveis — podem ser subidos via compose (ver abaixo).

### Docker Compose (recomendado)

```bash
docker compose up --build
```

Serviços expostos:

| Serviço  | Porta host → container |
|----------|------------------------|
| vintipay | 18443 → 8443           |
| keycloak | 18080 → 8080           |
| mysql    | 13306 → 3306           |
| kafka    | 9092 / 9093            |

### Provisionamento do Keycloak

Após o Keycloak estar saudável, execute o script de inicialização para criar realm, client e roles:

```bash
./keycloak-init.sh
```

## Testes

```bash
./mvnw test
```

Cobertura com JUnit 5 + Mockito; dependências externas (Keycloak, Stripe, Kafka) são mockadas.

## Profiles

`local`, `dev`, `prod`, `qld`, `render`, `resetdb` — ver `src/main/resources/application-*.yml`.

## Documentação

- `docs/adrs/` — Architecture Decision Records
- `docs/WEBHOOK_KAFKA_ARCHITECTURE.md` — arquitetura de webhooks via Kafka
- `.devin/` — contexto, convenções e skills do agente Devin
