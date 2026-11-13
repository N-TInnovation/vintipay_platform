# Convenções do Projeto

## Pacotes
- `cv.ntinnovation.vintipay.GatewayPaymentApplication` — ponto de entrada.
- `adapter.in.web.controller` — controllers REST/Thymeleaf.
- `adapter.in.web.dto` — DTOs de entrada/saída.
- `adapter.in.web.mapper` — conversores DTO/Domain.
- `adapter.in.kafka` / `adapter.in.messaging` — consumers.
- `adapter.out.persistence` — entidades JPA, repositories Spring Data, adapters.
- `adapter.out.client` — Feign clients.
- `application.port.in` — interfaces de use case.
- `application.port.out` — interfaces de repository/client.
- `application.core.domain` — entidades, enums, exceções, regras.
- `application.core.usecase` — implementações de use cases.
- `application.core.service` — serviços auxiliares.
- `config` — beans, security, etc.

## Nomenclatura
- Controllers terminam com `Controller`.
- Use cases terminam com `UseCase` (interface) e `UseCaseImpl` (implementação).
- Ports terminam com `Port`.
- Repositories JPA terminam com `JpaRepository`.
- Adapters terminam com `Adapter`.
- DTOs terminam com `DTO`.
- Mappers terminam com `Mapper`.

## Banco de dados
- Toda query SQL em `Repository`.
- Não escrever SQL em service ou controller.
- Nomes de tabelas e colunas em snake_case.
- IDs do tipo `Long` ou `BigInteger` conforme necessidade.

## Flyway
- Padrão de nome: `V<versao>__<descricao>.sql`.
- Versões inteiros contínuos: `V1`, `V2`, ... `V15`.
- DDL portável para H2; MySQL-only em scripts com versionamento separado quando necessário.

## Frontend
- Usar fragmentos de head/favicon em todos os templates.
- Formulários com validação Thymeleaf.
- Tratamento de erros com mensagens claras.

## Testes
- JUnit 5 + Mockito.
- Cobrir: sucesso, validação, exceção.
- Mockar dependências externas (Keycloak, Stripe, Feign, Kafka).
