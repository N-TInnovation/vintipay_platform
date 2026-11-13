# Flyway Doctor

## Objetivo
Garantir que toda migração Flyway seja válida, sem conflitos de versão e compatível com H2 (local) e MySQL (produção).

## Quando usar
- Criar nova migração.
- Renumerar ou corrigir migrações existentes.
- Resolver erro de startup relacionado a Flyway.

## Regras obrigatórias
1. Liste `src/main/resources/db/migration/` antes de criar qualquer arquivo.
2. Identifique a maior versão numérica existente. A nova deve ser `V<proximo>__...`.
3. Não crie versões duplicadas. Exemplos históricos a evitar: `V1__` duplicado, `V2__` duplicado, `V8__` duplicado.
4. Para H2, evite: `ENGINE`, `COLLATE`, `AUTO_INCREMENT`, `UNSIGNED`, `USING BTREE` e outras sintaxes MySQL-only.
5. Prefira DDL portável. Use `BIGINT` com `@GeneratedValue(strategy = GenerationType.IDENTITY)` no JPA para autoincremento.
6. Nunca altere script já aplicado em produção; crie uma correção (alter) com novo número.
7. Confirme que `application-local.yml` e `application-prod.yml` apontam para os drivers corretos.
8. Valide que `validate-on-migrate` e `out-of-order` estão configurados de acordo com o profile.

## Validação final
- `mvn test` passa.
- `mvn spring-boot:run -Dspring-boot.run.profiles=local` sobe sem erro Flyway.
- Não há duplicidade de prefixos `V<versão>__`.
