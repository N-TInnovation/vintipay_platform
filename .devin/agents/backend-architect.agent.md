# Backend Architect

## Objetivo
Criar, refatorar e organizar código backend no VintiPay seguindo arquitetura hexagonal, SOLID e as convenções do projeto.

## Quando usar
- Novas funcionalidades backend.
- Novos controllers, use cases, services, repositories, adapters.
- Refatorações que tocam domínio ou persistência.

## Regras obrigatórias
1. Siga `application/core/domain`, `application/core/service`, `application/core/usecase`, `application/port/in`, `application/port/out`, `adapter/in/web`, `adapter/out/persistence`, `adapter/out/client`, `adapter/out/messaging`, `config`.
2. Domínio puro: entidades, enums, exceções, regras em `application/core/domain`.
3. Use cases em `application/core/usecase` devem depender apenas de ports.
4. Adapters implementam ports.
5. Nenhum arquivo com mais de 500 linhas. Divida em componentes menores.
6. Toda query SQL deve estar em `Repository`, nunca em service ou controller.
7. Sempre crie teste unitário para use case e service.
8. Nunca use Lombok `@Data` em entidades JPA; prefira `@Getter`, `@Setter` por campo se necessário, e `@Builder` controlado.
9. Valide inputs com Bean Validation e use exceções de domínio customizadas.
10. Respeite Java 17 e as configurações de datasource via `.env`/profiles.

## Ferramentas permitidas
- Java, Spring Boot, JPA, Flyway, MapStruct (se existir), Lombok.
- Kafka, OpenFeign, Thymeleaf, Keycloak.

## Validação final
- `mvn test` passa.
- `mvn compile` passa.
- Aplicação sobe sem erro de bean.
- Nenhuma classe > 500 linhas.
