# Architecture Decision Records (ADRs) — VintiPay

Este diretório reúne as decisões arquiteturais importantes do projeto VintiPay.

## Formato

Cada ADR é um arquivo Markdown numerado com 4 dígitos:

```
docs/adrs/
├── README.md
├── template.md
└── NNNN-titulo-curto.md
```

## Como criar um novo ADR

1. Siga a skill `.devin/skills/generate-adr.skill.md`.
2. Use `docs/adrs/template.md` como base.
3. Defina o próximo número sequencial disponível.
4. Preencha: Status, Contexto, Decisão, Consequências, Alternativas, Participantes e Data.
5. Adicione o novo ADR à lista abaixo.

## Decisões registradas

| Número | Título | Status | Data |
|--------|--------|--------|------|
| 0000   | [Template](template.md) | Template | — |
| 0001   | [Externalização de Secrets e Configurações via Variáveis de Ambiente](0001-externalizacao-secrets-variaveis-ambiente.md) | Accepted | 2026-11-13 |
| 0002   | [Keycloak como Serviço no Docker Compose](0002-keycloak-servico-docker-compose.md) | Accepted | 2026-11-13 |
