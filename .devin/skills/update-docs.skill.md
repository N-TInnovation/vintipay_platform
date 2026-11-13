# update-docs

## Uso
Atualizar a documentação do projeto sempre que uma mudança afetar arquitetura, API, banco de dados, configuração, fluxo de negócio ou convenções.

## Input
- Tarefa executada.
- Arquivos alterados.
- Mudanças em endpoints, entidades, migrações, configurações, skills ou memórias.

## Quando atualizar
Sempre que:
- Um novo endpoint REST for criado ou alterado.
- Uma entidade, tabela ou migração Flyway for adicionada/modificada.
- O fluxo de autenticação, pagamento, webhook, integração ou assinatura mudar.
- Uma nova convenção ou regra de projeto for introduzida.
- Um novo agente, skill ou spec for criado.
- Um problema conhecido for resolvido ou descoberto.

## Passos
1. Identifique o impacto da mudança:
   - API/Controller → `docs/` ou OpenAPI.
   - Banco de dados → `src/main/resources/db/migration/` e `memory/known-issues.md`.
   - Arquitetura/Convenções → `.devin/memory/conventions.md`.
   - Integração (Stripe, Vendus, SISP, Kafka) → `docs/` ou `README.md` do módulo.
   - Novo agente/skill/spec → `.devin/README.md` e `memory/project-context.md`.
2. Atualize o documento mais específico primeiro.
3. Se houver `README.md` ou `HELP.md` genérico, atualize quando necessário.
4. Se a mudança resolve um problema conhecido, remova-o ou marque-o em `.devin/memory/known-issues.md`.
5. Se a mudança introduz um novo padrão de erro, adicione-o a `.devin/memory/known-issues.md`.
6. Verifique se o `OpenAPI/Springdoc` reflete os novos endpoints (se anotações `@Operation` foram adicionadas).
7. Nunca deixe documentação desatualizada sem avisar.

## Validação
- A documentação reflete o estado atual do código.
- Não há informações contraditórias entre docs, memórias e código.
- `mvn test` ainda passa.
