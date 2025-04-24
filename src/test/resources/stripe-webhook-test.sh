#!/bin/bash

# Script para testar o webhook do Stripe simulando eventos de assinatura
# Uso: ./stripe-webhook-test.sh [tipo_evento]
# Exemplo: ./stripe-webhook-test.sh checkout.session.completed

# Configurações
WEBHOOK_URL="https://localhost:8443/webhook/stripe"
STRIPE_SIGNATURE="whsec_test_signature"

# Verificar se o tipo de evento foi fornecido
EVENT_TYPE=${1:-"checkout.session.completed"}

echo "Simulando evento Stripe: $EVENT_TYPE"

# Selecionar o payload com base no tipo de evento
case "$EVENT_TYPE" in
  "checkout.session.completed")
    PAYLOAD='{
      "id": "evt_test123",
      "object": "event",
      "api_version": "2020-08-27",
      "created": 1619193715,
      "data": {
        "object": {
          "id": "cs_test_a1b2c3",
          "object": "checkout.session",
          "customer": "cus_test123",
          "subscription": "sub_test123",
          "client_reference_id": "user_123",
          "payment_status": "paid",
          "mode": "subscription"
        }
      },
      "livemode": false,
      "type": "checkout.session.completed"
    }'
    ;;
    
  "customer.subscription.created")
    PAYLOAD='{
      "id": "evt_test456",
      "object": "event",
      "api_version": "2020-08-27",
      "created": 1619193716,
      "data": {
        "object": {
          "id": "sub_test456",
          "object": "subscription",
          "customer": "cus_test456",
          "status": "active",
          "current_period_start": 1619193716,
          "current_period_end": 1621785716
        }
      },
      "livemode": false,
      "type": "customer.subscription.created"
    }'
    ;;
    
  "customer.subscription.updated")
    PAYLOAD='{
      "id": "evt_test789",
      "object": "event",
      "api_version": "2020-08-27",
      "created": 1619193717,
      "data": {
        "object": {
          "id": "sub_test456",
          "object": "subscription",
          "customer": "cus_test456",
          "status": "active",
          "current_period_start": 1619193717,
          "current_period_end": 1621785717
        }
      },
      "livemode": false,
      "type": "customer.subscription.updated"
    }'
    ;;
    
  "invoice.payment_succeeded")
    PAYLOAD='{
      "id": "evt_invoice123",
      "object": "event",
      "api_version": "2020-08-27",
      "created": 1619193718,
      "data": {
        "object": {
          "id": "in_test123",
          "object": "invoice",
          "customer": "cus_invoice123",
          "subscription": "sub_invoice123",
          "paid": true,
          "status": "paid"
        }
      },
      "livemode": false,
      "type": "invoice.payment_succeeded"
    }'
    ;;
    
  "customer.subscription.deleted")
    PAYLOAD='{
      "id": "evt_delete123",
      "object": "event",
      "api_version": "2020-08-27",
      "created": 1619193719,
      "data": {
        "object": {
          "id": "sub_test456",
          "object": "subscription",
          "customer": "cus_test456",
          "status": "canceled",
          "canceled_at": 1619193719
        }
      },
      "livemode": false,
      "type": "customer.subscription.deleted"
    }'
    ;;
    
  *)
    echo "Tipo de evento não suportado: $EVENT_TYPE"
    echo "Tipos suportados: checkout.session.completed, customer.subscription.created, customer.subscription.updated, invoice.payment_succeeded, customer.subscription.deleted"
    exit 1
    ;;
esac

# Enviar a requisição para o webhook
echo "Enviando requisição para $WEBHOOK_URL"
curl -X POST "$WEBHOOK_URL" \
  -H "Content-Type: application/json" \
  -H "Stripe-Signature: $STRIPE_SIGNATURE" \
  -d "$PAYLOAD" \
  --insecure \
  -v

echo -e "\nRequisição enviada com sucesso!"
