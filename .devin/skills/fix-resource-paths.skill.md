# fix-resource-paths

## Uso
Corrigir caminhos de arquivos (JSON, Excel, CSV, SQL) para funcionarem em local, Docker e Render.

## Input
- Classe que carrega o recurso.
- Nome do arquivo esperado.

## Passos
1. Localize a classe que lê o recurso (ex: `DataImportConfig`, `LocationDataImporter`).
2. Tente carregar do classpath primeiro com `ClassPathResource`.
3. Se não encontrar, use fallback em `System.getProperty("user.dir") + "/<pasta>"`.
4. Para leitura de JSON/Excel, verifique entidade alvo e tipos de campos (`int` vs `Long`, `Double` vs `String`).
5. Para SQL, use `ResourceDatabasePopulator` ou `ScriptUtils` com classpath fallback.
6. Adicione logs informativos: `INFO` quando encontrar, `ERROR` quando não.
7. Não use caminhos absolutos de ambiente de desenvolvedor.

## Validação
- `mvn test` passa.
- `mvn spring-boot:run -Dspring-boot.run.profiles=local` sobe.
- Recurso é localizado tanto no IDE quanto no Docker.
