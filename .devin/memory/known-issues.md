# Problemas Conhecidos

## 1. Flyway com versões duplicadas
- Sintomas: app não sobe, erro de duplicidade de migração.
- Causa: múltiplos arquivos `V1__...`, `V2__...`, `V8__...`.
- Solução: renumerar para próximo número disponível e garantir unicidade.
- Histórico:
  - `V1__alter_configuracao_pagamento.sql` → `V12__...`
  - `V1__modify_terminal_and_service_fields.sql` → `V11__...`
  - `V8__remove_metodo_pagamento_from_intencao.sql` → `V13__...`
  - `V2__create_location_tables.sql` → backup como `V14__...`

## 2. Scripts H2 vs MySQL
- Sintomas: Flyway falha localmente com H2.
- Causa: DDL com `ENGINE`, `COLLATE`, `AUTO_INCREMENT`, etc.
- Solução: usar DDL portável no profile local.

## 3. Caminhos de arquivos quebram no Docker/Render
- Sintomas: `FileNotFoundException` para JSON, Excel, SQL.
- Causa: caminhos absolutos hardcoded.
- Solução: tentar `ClassPathResource` primeiro; fallback em `user.dir + "/pasta"`.
- Classes afetadas: `DataImportConfig`, `LocationDataImporter`.

## 4. Importação de dados geográficos
- Arquivos em `/src/main/resources/static/json/`.
- Verificar campos reais das entidades (`zone`, `subzone`, `name`) em vez de campos inexistentes (`place`).
- Converter tipos corretamente (`int` para `Long`, `Double` para `String` quando necessário).

## 5. Favicons em templates
- Devem usar `fragments/head-favicons :: favicons` para consistência.
- Templates atualizados: checkout, registro, recibo, login, dashboard, erros, retorno, assinaturas.

## 6. Integração Vendus
- Gerenciada por `configuracao_aplicacao` vinculada a `configuracao_pagamento`.
- Rota: `/user/integracoes/novo` e `/user/integracoes`.
- Verificar `ConfiguracaoAplicacaoEntity`, `IntegracaoEnum` e `UserIntegracoesController`.
