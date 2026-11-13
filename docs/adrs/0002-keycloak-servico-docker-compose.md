# 0002. Keycloak como Serviço no Docker Compose

## Status
Accepted

## Contexto
A autenticação e autorização do VintiPay são delegadas ao Keycloak (OAuth2 Client + Resource Server). Anteriormente o ambiente dependia de uma instância externa instalada manualmente, o que gerava setup inconsistente entre desenvolvedores e dificultava a reprodução do ambiente completo. Além disso, o browser do usuário e o container da aplicação acessam o Keycloak por hostnames diferentes (`localhost` vs. `vintipay-keycloak`), o que exigia URLs distintas para autorização e para emissão/validação de tokens.

## Decisão
Adicionar o Keycloak como serviço do `docker-compose.yml`:

- Imagem pinada `quay.io/keycloak/keycloak:23.0.1` em modo `start-dev`.
- Persistência `KC_DB=dev-file` (desenvolvimento).
- Porta host `18080` → container `8080`, com healthcheck.
- `vintipay` depende de `keycloak: service_healthy`.
- Separação de URLs: `KEYCLOAK_AUTH_SERVER_URL` (comunicação interna container→container) e `KEYCLOAK_FRONTEND_URL` (URL exposta ao browser para o fluxo de autorização).
- Provisionamento de realm/client/roles via script `keycloak-init.sh`.

## Consequências

### Positivas
- Ambiente completo com um único `docker compose up`.
- Setup reproduzível e igual para todos os desenvolvedores.
- URLs internas e externas explicitamente separadas, evitando erros de redirect/issuer.

### Negativas
- Maior consumo de recursos locais (JVM extra).
- `dev-file` não é adequado para produção — produção exige Keycloak com banco dedicado.

### Riscos
- Versão pinada exige atualização coordenada — mitigação: bump explícito com testes.
- O script `keycloak-init.sh` ainda contém credenciais provisionadas — mitigação: migrar o secret do client para variável de ambiente (ver ADR 0001).

## Alternativas consideradas

### Keycloak externo instalado manualmente
- **Descrição**: cada dev instala e configura o próprio Keycloak.
- **Prós**: sem dependência no compose.
- **Contras**: setup inconsistente, propenso a erro, onboarding lento.
- **Motivo da rejeição**: não resolve a necessidade de reprodutibilidade.

### Import automático de realm via volume (`--import-realm`)
- **Descrição**: montar um `realm-export.json` no container para provisionamento automático.
- **Prós**: elimina o script de inicialização.
- **Contras**: export de realm versionado pode conter secrets e fica desatualizado com facilidade.
- **Motivo da rejeição**: mantido o script imperativo por ora; o import pode ser adotado como complemento futuro.

### Testcontainers apenas para testes
- **Descrição**: subir Keycloak apenas nos testes de integração.
- **Prós**: isolamento de testes.
- **Contras**: não atende ao desenvolvimento manual diário.
- **Motivo da rejeição**: escopo diferente; não é excludente.

## Participantes
- Equipe VintiPay / Devin (agente)

## Data
2026-11-13
