# 0001. Externalização de Secrets e Configurações via Variáveis de Ambiente

## Status
Accepted

## Contexto
O `docker-compose.yml` e o `application-local.yml` continham credenciais hardcoded de serviços externos: chave de API da Brevo, chave de API e webhook secret da Stripe, client secret do Keycloak e chave da API Vendus. Isso expunha secrets no repositório git (incluindo o histórico), dificultava a rotação de credenciais e impedia configurações distintas por ambiente sem alterar código.

## Decisão
Todas as secrets e configurações sensíveis passam a ser injetadas por variáveis de ambiente (`${VAR}`), sem valores default para credenciais. Arquivos `.env` e `.env.docker` foram adicionados ao `.gitignore`, e `.env.example` / `.env.docker.example` foram criados como referência documentada das variáveis necessárias. O `docker-compose.yml` passa a consumir `.env.docker` via `env_file`.

## Consequências

### Positivas
- Secrets fora do código-fonte e do versionamento.
- Configuração por ambiente sem alterar arquivos versionados.
- Onboarding documentado pelos arquivos `.env.example`.

### Negativas
- Setup local exige etapa manual de copiar e preencher o `.env`.
- Falha em definir uma variável obrigatória causa erro em runtime/startup.

### Riscos
- Secrets já commitadas permanecem no histórico do git — mitigação: rotacionar as credenciais expostas (Brevo, Stripe, Keycloak, Vendus).
- Arquivos `.env` podem vazar fora do versionamento — mitigação: acesso restrito e boas práticas de compartilhamento.

## Alternativas consideradas

### Vault / gerenciador de secrets dedicado (HashiCorp Vault, AWS Secrets Manager)
- **Descrição**: centralizar secrets em um serviço dedicado com rotação e auditoria.
- **Prós**: segurança máxima, rotação automática, auditoria.
- **Contras**: infraestrutura adicional, custo e complexidade desproporcionais ao estágio atual do projeto.
- **Motivo da rejeição**: over-engineering para o momento; pode ser adotado futuramente em produção sem mudar a abordagem (a aplicação já lê tudo via env vars).

### Spring Cloud Config Server
- **Descrição**: servidor central de configuração.
- **Prós**: configuração centralizada e versionável.
- **Contras**: mais um serviço para operar; config não sensível já é bem atendida pelos `application-*.yml`.
- **Motivo da rejeição**: resolve um problema que o projeto não tem; adiciona ponto único de falha.

### Manter secrets hardcoded
- **Motivo da rejeição**: risco de segurança inaceitável — credenciais reais já estavam expostas no histórico.

## Participantes
- Equipe VintiPay / Devin (agente)

## Data
2026-11-13
