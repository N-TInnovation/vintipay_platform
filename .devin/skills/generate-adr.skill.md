# generate-adr

## Uso
Criar um **Architecture Decision Record (ADR)** sempre que uma decisão arquitetural significativa for tomada no VintiPay. ADRs documentam o contexto, a decisão, as alternativas rejeitadas e as consequências, preservando o raciocínio para o futuro.

## Input
- Título da decisão.
- Contexto que gerou a necessidade.
- Alternativas consideradas.
- Decisão escolhida.
- Consequências (positivas, negativas, riscos).
- Participantes ou fontes relevantes.

## Quando usar
Sempre que a equipe ou o Devin decidir:
- Adotar uma nova tecnologia, biblioteca, ferramenta ou padrão.
- Mudar a arquitetura (ex: adicionar Kafka, mudar banco, novo gateway).
- Criar, remover ou renomear camadas/pacotes.
- Alterar contratos de API, integrações ou fluxos de negócio.
- Escolher entre alternativas técnicas com trade-offs relevantes.

## Passos

### 1. Verificar se já existe ADR similar
- Liste arquivos em `docs/adrs/`.
- Busque por palavras-chave relacionadas.
- Se existir, atualize-o com nova decisão/superseded em vez de duplicar.

### 2. Definir o próximo número
- ADRs são numerados sequencialmente: `0001`, `0002`, `0003`, ...
- Não reutilize números.
- Use 4 dígitos para ordenação correta.

### 3. Preencher o template
Crie `docs/adrs/NNNN-titulo-curto.md` com base em `docs/adrs/template.md`:

```markdown
# NNNN. Título da Decisão

## Status
Proposed | Accepted | Deprecated | Superseded by [NNNN-titulo](NNNN-titulo.md)

## Contexto
Explique o problema, restrições e objetivos.

## Decisão
Descreva a decisão tomada de forma clara e objetiva.

## Consequências
- Positivas
- Negativas
- Riscos

## Alternativas consideradas
Para cada alternativa:
- Descrição.
- Prós.
- Contras.
- Por que foi rejeitada.

## Participantes
- Nome / função.

## Data
YYYY-MM-DD
```

### 4. Revisar riscos e compatibilidade
- Verifique se a decisão conflita com `memory/conventions.md` ou `memory/known-issues.md`.
- Se conflitar, atualize esses arquivos ou discuta com o usuário.

### 5. Atualizar índice
- Adicione o novo ADR a `docs/adrs/README.md` na lista de decisões.

### 6. Comunicar
- Se o ADR for crítico, mencione-o no resposta ao usuário.
- Se a decisão alterar o escopo original, peça confirmação.

## Validação
- O arquivo segue o formato de `docs/adrs/template.md`.
- Status está coerente (Proposed/Accepted/Deprecated/Superseded).
- Número é único e sequencial.
- README do ADR foi atualizado.
- Decisão está alinhada às convenções do projeto.
