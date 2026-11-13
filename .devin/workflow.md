# Devin Workflow — VintiPay

Este workflow define como o Devin deve agir ao receber uma tarefa no projeto VintiPay.

## 1. Entender a tarefa
- Leia a mensagem do usuário e identifique o domínio: backend, frontend, banco de dados, testes, infra.
- Se a tarefa for vaga, peça esclarecimento ou converta em `.devin/specs/<tarefa>.spec.md` com hipóteses claras.

## 2. Carregar contexto
Sempre leia antes de agir:
- `.devin/memory/project-context.md`
- `.devin/memory/stack.md`
- `.devin/memory/known-issues.md`
- `.devin/memory/conventions.md`

## 3. Escolher o agente
Use o agente correto conforme o escopo:
- `backend-architect.agent.md` → código backend, domínio, use cases, repositories.
- `flyway-doctor.agent.md` → migrações Flyway.
- `frontend-fixer.agent.md` → templates Thymeleaf, CSS, JS, favicons.
- `test-runner.agent.md` → testes, regressão, validação.

## 4. Escolher a skill
Siga a skill apropriada passo a passo:
- `add-hexagonal-feature.skill.md`
- `add-flyway-migration.skill.md`
- `fix-resource-paths.skill.md`
- `run-tests.skill.md`
- `update-thymeleaf-fragment.skill.md`
- `update-docs.skill.md`
- `analyze-improvements-and-risks.skill.md`
- `generate-adr.skill.md`

## 5. Analisar melhorias e riscos
- Execute `analyze-improvements-and-risks.skill.md` para tarefas não triviais.
- Liste alternativas, prós e contras.
- Avalie riscos de segurança, performance, compatibilidade, regressão, manutenibilidade e negócio.
- Documente a análise no spec e, se houver risco crítico, comunique ao usuário antes de prosseguir.

## 6. Executar com cautela
- Faça a menor mudança possível para atender à tarefa.
- Não refatore código não relacionado.
- Não exclua arquivos sem confirmação do usuário.
- Respeite o tamanho máximo de 500 linhas por arquivo.

## 7. Validar
- Antes de finalizar, execute `mvn test`.
- Se houver falha, corrija e rode novamente.
- Para tarefas Flyway, suba a aplicação no profile `local` e verifique o startup.
- Para tarefas frontend, verifique se o template renderiza sem erros Thymeleaf.

## 8. Documentar
- Execute `update-docs.skill.md` sempre que a mudança afetar APIs, banco de dados, arquitetura, fluxo de negócio, configuração ou convenções.
- Execute `generate-adr.skill.md` toda vez que uma decisão arquitetural relevante for tomada (nova tecnologia, mudança de padrão, novo gateway, etc.).
- Se descobrir um novo padrão de erro ou solução, adicione a `.devin/memory/known-issues.md`.
- Se criar uma nova convenção, adicione a `.devin/memory/conventions.md`.
- Se criar um novo agente, skill ou spec, atualize `.devin/README.md`.

## 9. Responder
- Resuma o que foi feito.
- Liste os arquivos alterados.
- Informe o resultado dos testes.
- Informe o próximo passo recomendado.
