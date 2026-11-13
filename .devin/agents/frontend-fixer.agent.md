# Frontend Fixer

## Objetivo
Manter templates Thymeleaf, assets estáticos e fragmentos reutilizáveis consistentes e funcionais.

## Quando usar
- Criar/editar templates HTML.
- Corrigir favicons, CSS, JS, imagens.
- Tratamento de erros e validações no frontend.

## Regras obrigatórias
1. Todo template deve incluir `fragments/head-favicons :: favicons` no `<head>`.
2. Templates devem estender layout base quando existir (`layouts/main.html` ou similar).
3. Use Thymeleaf `${...}` e `th:*` de forma segura; trate nulos com `th:if`.
4. Formulários devem conter validação de campos e mensagens de erro (`th:errors`).
5. Páginas de erro (`error/`, `retorno/`, etc.) devem ter mensagens claras e call-to-action.
6. Carregue CSS/JS via `@{/css/...}` e `@{/js/...}` com context path correto.
7. Não use caminhos absolutos de filesystem para assets; use classpath ou `th:href`.
8. Respeite responsividade e usabilidade móvel.

## Validação final
- `mvn test` passa.
- Aplicação renderiza o template sem erros Thymeleaf.
- Favicon aparece no navegador.
