# Arquitetura de Webhooks com Kafka

## Visão Geral

A arquitetura de webhooks foi implementada utilizando Kafka como mensageria assíncrona para processamento de eventos de pagamento. Isso garante que os webhooks sejam enviados de forma resiliente e desacoplada.

## Componentes

### 1. Docker Compose
Configurados 3 serviços:
- **Zookeeper**: Gerenciador de metadados do Kafka
- **Kafka**: Broker de mensagens
- **VintiPay App**: Aplicação principal

**Tópico**: `webhook-pagamento`

### 2. Fluxo de Dados

```
[Pagamento Processado]
        ↓
[WebhookUseCase] → [WebhookKafkaAdapter]
        ↓
[Kafka Topic: webhook-pagamento]
        ↓
[WebhookKafkaConsumer]
        ↓
[WebhookAdapter (HTTP)]
        ↓
[URL Externa do Cliente]
```

### 3. Componentes Java

#### **Producer (Publicação de Eventos)**

1. **WebhookKafkaProducer**
   - Publica eventos no tópico `webhook-pagamento`
   - Serializa objetos `WebhookEventoDTO` como JSON

2. **WebhookKafkaAdapter**
   - Implementa `WebhookServicePort`
   - Marcado como `@Primary` para ser usado pelo use case
   - Converte `Pagamento` em `WebhookEventoDTO`

3. **WebhookEventoDTO**
   - `webhookUrl`: URL de destino
   - `tipo`: Tipo do evento (ex: "pagamento")
   - `payload`: Dados do pagamento
   - `timestamp`: Data/hora do evento

#### **Consumer (Processamento de Eventos)**

1. **WebhookKafkaConsumer**
   - Escuta o tópico `webhook-pagamento`
   - Deserializa `WebhookEventoDTO`
   - Chama `WebhookAdapter` (HTTP) para enviar o webhook
   - **Acknowledgment Manual**: Só confirma a mensagem em caso de sucesso
   - Se houver erro, a mensagem permanece no tópico para reprocessamento

2. **WebhookAdapter (HTTP)**
   - Implementa `WebhookServicePort`
   - Nomeado como `webhookHttpAdapter` via `@Qualifier`
   - Faz a chamada HTTP real usando Feign Client

#### **Configuração de Resiliência**

1. **Acknowledgment Manual (AckMode.MANUAL)**
   - `ENABLE_AUTO_COMMIT_CONFIG: false`
   - Mensagens só são confirmadas após processamento bem-sucedido
   - Em caso de erro, mensagem permanece no tópico

2. **Parâmetros de Poll**
   - `MAX_POLL_INTERVAL_MS_CONFIG: 300000` (5 minutos)
   - `MAX_POLL_RECORDS_CONFIG: 10` (até 10 registros por vez)
   - `Concurrency: 3` (3 threads processando em paralelo)

#### **Use Case**

1. **WebhookUseCase**
   - Interface do caso de uso
   - Define `enviarWebhook(String url, Pagamento payload)`

2. **WebhookUseCaseImpl**
   - Implementação que injeta `WebhookServicePort`
   - Por padrão usa `WebhookKafkaAdapter` (marcado como @Primary)

### 4. Configurações

#### **application-local.yml**
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9093
    consumer:
      group-id: vintipay-webhook-group
      auto-offset-reset: earliest
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

#### **docker-compose.yml**
```yaml
kafka:
  ports:
    - "9092:9092"  # Porta interna (containers)
    - "9093:9093"  # Porta externa (host)
  environment:
    KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"
```

## Vantagens da Arquitetura

1. **Resiliência com Acknowledgment Manual**: 
   - Mensagem só é confirmada após envio bem-sucedido do webhook
   - Em caso de erro, mensagem permanece no tópico para reprocessamento automático
   - Evita perda de eventos mesmo em caso de falhas
   
2. **Desacoplamento**: O processamento do pagamento não espera pelo envio do webhook

3. **Escalabilidade**: 
   - 3 threads processando webhooks em paralelo
   - Múltiplos consumers podem ser adicionados aumentando o número de partições
   
4. **Rastreabilidade**: Todos os eventos são registrados no Kafka com logs detalhados

5. **Performance**: 
   - Processamento assíncrono não bloqueia o fluxo principal
   - Processa até 10 mensagens por poll para otimizar throughput
   
6. **Controle de Backpressure**: 
   - Limite de tempo de processamento (5 minutos por poll)
   - Quantidade controlada de registros por poll

## Como Usar

### 1. Iniciar os Serviços
```bash
docker-compose up -d
```

### 2. Verificar Tópicos Kafka
```bash
docker exec -it vintipay-kafka kafka-topics --bootstrap-server localhost:9092 --list
```

### 3. Consumir Mensagens (Debug)
```bash
docker exec -it vintipay-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic webhook-pagamento \
  --from-beginning
```

### 4. Enviar Webhook via Controller
```java
@PostMapping("/webhook/pagamentos")
public void processarPagamento(@RequestBody Pagamento pagamento) {
    // Processa o pagamento
    // ...
    
    // Envia webhook de forma assíncrona
    if (configuracao.getWebhookAtivo()) {
        webhookUseCase.enviarWebhook(
            configuracao.getWebhookUrl(), 
            pagamento
        );
    }
}
```

## Monitoramento

### Logs
- **Producer**: Logs em `WebhookKafkaProducer`
- **Consumer**: Logs em `WebhookKafkaConsumer`
- **HTTP**: Logs em `WebhookAdapter`

### Métricas Kafka
```bash
# Ver consumer groups
docker exec -it vintipay-kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --list

# Ver detalhes do group
docker exec -it vintipay-kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --group vintipay-webhook-group \
  --describe
```

## Como Funciona o Acknowledgment Manual

```java
@KafkaListener(topics = "webhook-pagamento")
public void consumirEventoWebhook(WebhookEventoDTO evento, Acknowledgment acknowledgment) {
    try {
        // 1. Processar o evento
        webhookHttpAdapter.enviarWebhook(evento.getWebhookUrl(), pagamento);
        
        // 2. Se sucesso, confirma a mensagem (remove do tópico)
        acknowledgment.acknowledge();
        log.info("✅ Mensagem confirmada");
        
    } catch (Exception e) {
        // 3. Se erro, NÃO confirma (mensagem fica no tópico)
        log.error("❌ Erro ao processar. Mensagem será reprocessada");
        // Kafka irá reenviar a mensagem após o próximo poll
    }
}
```

### Comportamento em Caso de Erro

1. **Erro HTTP (webhook offline)**: Mensagem permanece no tópico
2. **Timeout**: Mensagem não é confirmada, será reprocessada
3. **Exceção de parsing**: Mensagem não é confirmada
4. **Aplicação reinicia**: Mensagens não confirmadas são reprocessadas

### Offset Management

- **Sucesso**: Offset avança, mensagem é removida
- **Erro**: Offset não avança, mensagem permanece disponível
- **Rebalance**: Consumer retoma do último offset confirmado

## Troubleshooting

### Webhook não está sendo enviado
1. Verificar se o Kafka está rodando: `docker ps`
2. Verificar logs do producer: `docker logs vintipay-app | grep WebhookKafkaProducer`
3. Verificar se a mensagem chegou no tópico:
   ```bash
   docker exec -it vintipay-kafka kafka-console-consumer \
     --bootstrap-server localhost:9092 \
     --topic webhook-pagamento \
     --from-beginning
   ```

### Consumer não está processando
1. Verificar logs do consumer: `docker logs vintipay-app | grep WebhookKafkaConsumer`
2. Verificar se o consumer group está ativo:
   ```bash
   docker exec -it vintipay-kafka kafka-consumer-groups \
     --bootstrap-server localhost:9092 \
     --group vintipay-webhook-group \
     --describe
   ```
3. Verificar LAG (mensagens pendentes)

### Mensagens sendo reprocessadas infinitamente
1. Verificar se o webhook está sempre falhando
2. Implementar Dead Letter Queue para mensagens com muitas tentativas
3. Adicionar contador de retry no payload

### Erro na chamada HTTP
1. Verificar se a URL do webhook está correta
2. Verificar se o endpoint externo está acessível
3. Adicionar timeout adequado nas requisições HTTP
4. Verificar logs detalhados em `WebhookAdapter`

## Melhorias Futuras

1. **Dead Letter Queue (DLQ)**: Para webhooks que falharam múltiplas vezes
2. **Retry Policy**: Configurar tentativas automáticas com backoff exponencial
3. **Circuit Breaker**: Evitar sobrecarga em webhooks que estão falhando
4. **Webhook Signatures**: Adicionar HMAC para segurança
5. **Idempotência**: Garantir que webhooks duplicados não causem problemas
