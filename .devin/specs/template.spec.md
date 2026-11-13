# Template de Spec

Use este arquivo como base para qualquer nova tarefa no VintiPay.

## Cabeçalho

```yaml
spec:
  id: nome-unico-da-tarefa
  tipo: feature | bugfix | refactor | test | docs
  agente: backend-architect | flyway-doctor | frontend-fixer | test-runner
  skills:
    - skill-1
    - skill-2
```

## Campos

- **id**: identificador único, snake_case, sem espaços.
- **tipo**: tipo da mudança.
- **agente**: agente responsável pela execução.
- **skills**: lista de skills que serão executadas.
- **titulo**: resumo curto.
- **descricao**: objetivo e contexto da tarefa.
- **escopo**: pacotes, arquivos e domínios envolvidos.
- **criterios_aceite**: lista verificável de critérios.
- **restricoes**: limitações, compatibilidades e cuidados.
- **saida**: resultado esperado (código, diff, teste, documento).

## Exemplo de preenchimento

```markdown
## spec

```yaml
spec:
  id: nome-da-feature
  tipo: feature
  agente: backend-architect
  skills:
    - add-hexagonal-feature
    - run-tests
```

## titulo
Breve descrição.

## descricao
Contexto e o que deve ser entregue.

## escopo
- `src/main/java/cv/ntinnovation/vintipay/application/...`
- `src/main/java/cv/ntinnovation/vintipay/adapter/...`
- `src/test/java/...`

## criterios_aceite
- [ ] Critério 1.
- [ ] Critério 2.
- [ ] `mvn test` passa.

## restricoes
- Não alterar tabelas já aplicadas em produção.
- Manter compatibilidade H2 e MySQL.

## saida
Código funcional e testado.
```
