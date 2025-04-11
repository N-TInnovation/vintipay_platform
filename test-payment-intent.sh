#!/bin/bash

echo "Testing Payment Intent API"
echo "-------------------------"
echo "This script simulates a request to the payment intent endpoint"

# Define the endpoint URL (assuming the application would run on port 8080)
URL="http://localhost:8080/api/v1/pagamentos/intencao"

# Create a sample payment intent request in JSON format
JSON_DATA='{
  "amount": 100.50,
  "currency": "CVE",
  "metodoPagamentoId": 1,
  "contaPagamentoId": 1,
  "merchantRef": "MERCHANT123",
  "merchantSession": "SESSION456",
  "posId": "POS789",
  "entityCode": "1234",
  "transactionCode": "TRANS001",
  "referenceNumber": "REF123456"
}'

echo "Request payload:"
echo "$JSON_DATA" | jq '.' 2>/dev/null || echo "$JSON_DATA"
echo ""

echo "When the application is running, you can test the endpoint with:"
echo "curl -X POST -H \"Content-Type: application/json\" -d '$JSON_DATA' $URL"
echo ""
echo "Expected response would include:"
echo " - HTTP status 201 (Created)"
echo " - JSON response with payment intent details including an ID and reference"
