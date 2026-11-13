# add-flyway-migration

## Uso
Adicionar nova migração Flyway sem conflitos e compatível com H2/MySQL.

## Input
- Descrição da mudança no banco.
- DDL ou DML necessário.

## Passos
1. Listar `src/main/resources/db/migration/`.
2. Identificar a maior versão numérica (não lexicográfica).
3. Criar `V<proximo>__<descricao>.sql`.
4. Verificar duplicidade de `V<versao>__`.
5. Se for DDL portável, garantir compatibilidade com H2:
   - sem `ENGINE`, `COLLATE`, `AUTO_INCREMENT`, `UNSIGNED`;
   - sem `USING BTREE`, `CHARACTER SET`;
   - usar `BIGINT` para IDs autoincrementais;
   - chaves estrangeiras e constraints com nomes padronizados.
6. Se for MySQL-only e houver outro script H2, versionar separadamente.
7. Adicionar `application-local.yml` e `application-prod.yml` conforme driver ativo.
8. Rodar `mvn test` e `mvn spring-boot:run -Dspring-boot.run.profiles=local`.

## Validação
- Sem duplicidade de versão.
- App inicia localmente sem erro Flyway.
- `mvn test` passa.
