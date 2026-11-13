# update-thymeleaf-fragment

## Uso
Atualizar templates Thymeleaf para usar fragmentos reutilizáveis, como favicons e layouts.

## Input
- Template a atualizar.
- Fragmento a incluir.

## Passos
1. Verifique se o fragmento existe em `templates/fragments/`.
2. No `<head>` do template, substitua configurações individuais por `th:replace="fragments/head-favicons :: favicons"`.
3. Garanta que o template estende o layout base, se houver.
4. Valide `th:href` e `th:src` com `@{...}`.
5. Verifique `th:if` e `th:unless` para evitar null pointer.
6. Teste renderização carregando a página localmente.

## Validação
- `mvn test` passa.
- Aplicação inicia e renderiza o template.
- Nenhum `<head>` duplicado ou favicon ausente.
