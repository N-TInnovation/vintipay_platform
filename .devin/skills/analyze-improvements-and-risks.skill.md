# analyze-improvements-and-risks

## Uso
Antes de executar uma mudança no VintiPay, analisar possíveis melhorias técnicas e identificar riscos de negócio, segurança, performance, manutenibilidade e compatibilidade.

## Input
- Tarefa proposta (spec, mensagem ou contexto).
- Código e arquitetura existente.
- `memory/known-issues.md` e `memory/conventions.md`.

## Quando usar
- Antes de iniciar qualquer feature, refatoração, migração ou correção não trivial.
- Quando o escopo envolve múltiplos domínios, integrações externas ou mudanças de contrato.
- Quando há dúvida sobre a melhor abordagem técnica.

## Passos

### 1. Entender o problema
- Releia a tarefa e identifique a dor real: usuário final, operação, estabilidade, segurança ou performance.
- Liste as restrições de negócio e técnicas.

### 2. Mapear alternativas
- Defina pelo menos duas formas de resolver o problema.
- Para cada alternativa, liste:
  - **Prós**: facilidade, performance, manutenibilidade, segurança, reutilização.
  - **Contras**: complexidade, débito técnico, dependências, tempo de implementação.

### 3. Identificar riscos
Avalie riscos nas categorias:
- **Segurança**: vazamento de dados, bypass de autenticação, exposição de UUIDs/PII, tokens.
- **Performance**: N+1, carga no banco, processamento síncrono pesado, falta de cache.
- **Compatibilidade**: quebra de contrato, migrações Flyway conflitantes, H2 vs MySQL, Docker.
- **Regressão**: quebra de endpoints, templates, jobs Kafka, integrações (Stripe, Vendus, SISP).
- **Manutenibilidade**: classes > 500 linhas, acoplamento, duplicação de código, falta de testes.
- **Negócio**: impacto em recibos, pagamentos, assinaturas, webhooks.

### 4. Propor melhorias
- Simplifique o design mantendo o objetivo.
- Sugira aplicação de padrões (Strategy, Factory, Repository, Ports-and-Adapters).
- Indique onde extrair componentes menores se houver risco de arquivo grande.
- Verifique se a mudança pode ser feita em pequenos passos incrementais.

### 5. Documentar análise
- Registre a análise resumida em `.devin/specs/<tarefa>.spec.md` na seção `analise_melhorias_e_riscos`.
- Se o risco for crítico, comunique ao usuário antes de prosseguir.

## Validação
- A análise possui pelo menos duas alternativas e seus prós/contras.
- Riscos foram mapeados nas categorias acima.
- A alternativa escolhida está alinhada à arquitetura hexagonal e às convenções.
- O usuário foi avisado sobre riscos críticos, se houver.
