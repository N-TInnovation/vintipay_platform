# Devin Agents — VintiPay

Este diretório contém a arquitetura de agentes e skills do Devin para o projeto VintiPay.

## Propósito
Padronizar como o Devin atua no código do VintiPay, reduzindo erros recorrentes (Flyway, caminhos de arquivos, favicons, arquitetura) e garantindo qualidade com testes.

## Estrutura

```
.devin/
├── agents/          # personalidades especializadas do Devin
├── skills/          # passo a passo para ações comuns
├── memory/          # contexto, stack, problemas e convenções do projeto
├── workflow.md      # fluxo de trabalho do Devin
└── README.md        # este arquivo
```

## Como usar

1. O Devin lê `workflow.md` ao iniciar uma tarefa.
2. Depois lê `memory/` para carregar contexto.
3. Seleciona o `agent/` adequado.
4. Executa a `skill/` correspondente.
5. Sempre finaliza com `mvn test`.

## Agents

- `backend-architect` — desenvolvimento backend hexagonal.
- `flyway-doctor` — migrações seguras e sem conflito.
- `frontend-fixer` — templates Thymeleaf e assets.
- `test-runner` — execução e validação de testes.

## Skills

- `add-hexagonal-feature` — criar feature backend no padrão atual.
- `add-flyway-migration` — adicionar migração Flyway.
- `fix-resource-paths` — corrigir caminhos de JSON/Excel/SQL.
- `run-tests` — executar e validar `mvn test`.
- `update-thymeleaf-fragment` — atualizar templates com fragmentos.
- `update-docs` — atualizar documentação do projeto quando necessário.
- `analyze-improvements-and-risks` — analisar alternativas e riscos antes de executar.
- `generate-adr` — registrar decisões arquiteturais em `docs/adrs/`.

## Manutenção

Atualize `memory/known-issues.md` e `memory/conventions.md` sempre que novos padrões de erro ou decisões arquiteturais surgirem.
