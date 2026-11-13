# Contexto do Projeto

## Nome
VintiPay

## Descrição
Serviço de integração com gateway de pagamento SISP, focado em recibos, pagamentos, assinaturas e integrações (Stripe, Vendus, etc.).

## Arquitetura
Arquitetura hexagonal com Spring Boot:
- `adapter/in/` — controllers, Kafka consumers, mensageria.
- `adapter/out/` — persistência JPA, Feign clients, envio de mensagens, storage.
- `application/port/in` — use case interfaces.
- `application/port/out` — repository/client interfaces.
- `application/core/` — domínio, services, use cases.
- `config/` — beans de configuração.

## Padrões
- SOLID.
- Componentes pequenos (máximo 500 linhas).
- Padrões de projeto: Factory, Strategy, Singleton, Repository.
- DDD leve com entidades, enums, exceções e regras.
- Queries SQL apenas em repositories.

## Considerações gerais
- Sempre execute testes após alterações.
- Respeite profiles (`local`, `prod`, `docker`).
- Flyway deve manter compatibilidade H2 (local) e MySQL (produção).
- Frontend Thymeleaf com fragmentos reutilizáveis.
