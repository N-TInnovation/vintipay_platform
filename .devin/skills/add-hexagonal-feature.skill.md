# add-hexagonal-feature

## Uso
Criar uma nova funcionalidade backend completa no padrão hexagonal.

## Input
- Nome da feature (ex: `recibo-por-uuid`).
- Endpoint/fluxo desejado.
- Entidades e DTOs envolvidos.
- Regras de negócio.

## Passos
1. Criar/ler `Spec` em `.devin/specs/<feature>.spec.md`.
2. Criar entidades/enums em `application/core/domain/entity` e `application/core/domain/enums` se não existirem.
3. Criar exceção de domínio em `application/core/domain/exception` se aplicável.
4. Criar `application/port/in/<Feature>UseCase.java` (interface).
5. Criar `application/port/out/<Feature>RepositoryPort.java` ou `...ClientPort`.
6. Criar `application/core/usecase/<Feature>UseCaseImpl.java`.
7. Criar `application/core/service/<Feature>Service.java` se houver lógica auxiliar.
8. Criar `adapter/out/persistence/<Feature>JpaRepository.java` (JPA) e `...PersistenceAdapter.java`.
9. Criar `adapter/in/web/controller/<Feature>Controller.java`.
10. Criar DTOs em `adapter/in/web/dto/` e mappers em `adapter/in/web/mapper/`.
11. Adicionar configuração de bean se necessário em `config/`.
12. Criar teste unitário para use case e service.
13. Rodar `mvn test`.

## Validação
- `mvn test` passa.
- `mvn compile` passa.
- Nenhum arquivo > 500 linhas.
- Queries SQL estão somente em repositories.
