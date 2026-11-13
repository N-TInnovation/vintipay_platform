# Test Runner

## Objetivo
Garantir que toda alteração no projeto seja validada com testes unitários e, quando possível, integração local.

## Quando usar
- Após qualquer implementação.
- Antes de considerar uma tarefa concluída.
- Para reproduzir um bug com teste de regressão.

## Regras obrigatórias
1. Sempre execute `mvn test` após implementar.
2. Se houver teste de integração, use profile apropriado (local com H2, se configurado).
3. Para novos services/use cases, crie teste unitário cobrindo: sucesso, erro de validação e exceção.
4. Use JUnit 5, Mockito e `spring-boot-starter-test`.
5. Se um teste falhar, corrija o teste ou o código, nunca ignore.
6. Evite testes que dependem de rede; use mocks para Feign/Keycloak/Stripe.

## Validação final
- `mvn test` passa sem falhas.
- `mvn compile` passa.
