# run-tests

## Uso
Executar testes unitários e validar estabilidade do projeto.

## Input
- Nome do teste (opcional).
- Profile desejado (padrão: `local`).

## Passos
1. Certifique-se de que o Java 17 está ativo (`/home/natalio/.jdks/corretto-17.0.16`).
2. Execute `mvn test -Dspring.profiles.active=local`.
3. Se houver falhas, leia a stack trace.
4. Se a falha for em teste existente, corrija o código ou o teste.
5. Se a falha for por recurso ausente, verifique `src/test/resources` e profiles.
6. Rode `mvn clean compile` para garantir que o build gera classes.

## Validação
- `mvn test` passa.
- `mvn compile` passa.
