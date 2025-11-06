#!/bin/bash

# Script para aplicar alterações relacionadas a subscrições no arquivo checkout.html

CHECKOUT_FILE="src/main/resources/templates/links/checkout.html"

echo "Aplicando alterações no arquivo checkout.html..."

# 1. Adicionar o badge de assinatura na div product-details
sed -i '/class="product-price".*59\.99 CVE<\/p>/a \                        <!-- Badge de assinatura -->\n                        <th:block th:replace="~{fragments/subscription-details :: subscription-badge}"></th:block>' "$CHECKOUT_FILE"

# 2. Adicionar os detalhes completos da assinatura antes da div order-totals
sed -i '/<div class="order-totals">/i \                <!-- Detalhes completos da assinatura -->\n                <th:block th:replace="~{fragments/subscription-details :: subscription-details}"></th:block>\n' "$CHECKOUT_FILE"

# 3. Modificar o botão de submissão para mostrar "Subscrever" quando for uma assinatura
sed -i 's/<button type="submit" class="checkout-btn">\n                        Finalizar Compra\n                    <\/button>/<button type="submit" class="checkout-btn">\n                        <span th:if="${isSubscription}">Subscrever<\/span>\n                        <span th:unless="${isSubscription}">Finalizar Compra<\/span>\n                    <\/button>/' "$CHECKOUT_FILE"

echo "Alterações aplicadas com sucesso!"
