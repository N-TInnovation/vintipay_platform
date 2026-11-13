#!/bin/bash

# Script de inicialização do Keycloak para VintiPay
# Este script configura o realm, client e roles necessários

KEYCLOAK_URL="http://localhost:18080"
REALM_NAME="vintipay"
CLIENT_ID="vintipay-client"
CLIENT_SECRET="PSvxsR3bvLIsRiayytQVJ3oi45snoS52"

echo "Aguardando Keycloak ficar disponível..."
until curl -s "${KEYCLOAK_URL}/health/ready" > /dev/null; do
    echo "Keycloak ainda não está pronto, aguardando..."
    sleep 5
done

echo "Keycloak está pronto! Iniciando configuração..."

# Obter token de acesso admin
echo "Obtendo token de acesso admin..."
TOKEN=$(curl -s -X POST "${KEYCLOAK_URL}/realms/master/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "username=admin" \
    -d "password=admin" \
    -d "grant_type=password" \
    -d "client_id=admin-cli" | jq -r '.access_token')

if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
    echo "Erro: Não foi possível obter token de acesso"
    exit 1
fi

echo "Token obtido com sucesso!"

# Verificar se o realm já existe
echo "Verificando se o realm ${REALM_NAME} já existe..."
REALM_EXISTS=$(curl -s -o /dev/null -w "%{http_code}" "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}" \
    -H "Authorization: Bearer ${TOKEN}")

if [ "$REALM_EXISTS" == "200" ]; then
    echo "Realm ${REALM_NAME} já existe. Pulando criação."
else
    echo "Criando realm ${REALM_NAME}..."
    curl -s -X POST "${KEYCLOAK_URL}/admin/realms" \
        -H "Authorization: Bearer ${TOKEN}" \
        -H "Content-Type: application/json" \
        -d '{
            "realm": "'${REALM_NAME}'",
            "enabled": true,
            "displayName": "VintiPay",
            "loginTheme": "keycloak",
            "accountTheme": "keycloak",
            "emailTheme": "keycloak",
            "sslRequired": "external",
            "registrationAllowed": false,
            "registrationEmailAsUsername": false,
            "rememberMe": true,
            "verifyEmail": true,
            "loginWithEmailAllowed": true,
            "duplicateEmailsAllowed": false,
            "resetPasswordAllowed": true,
            "editUsernameAllowed": false,
            "bruteForceProtected": true,
            "permanentLockout": false,
            "maxFailureWaitSeconds": 900,
            "minimumQuickLoginWaitSeconds": 60,
            "waitIncrementSeconds": 60,
            "quickLoginCheckMilliSeconds": 1000,
            "maxDeltaTimeSeconds": 43200,
            "failureFactor": 5,
            "accessTokenLifespan": 300,
            "accessTokenLifespanForImplicitFlow": 900,
            "ssoSessionIdleTimeout": 1800,
            "ssoSessionMaxLifespan": 36000,
            "offlineSessionIdleTimeout": 2592000,
            "accessCodeLifespan": 60,
            "accessCodeLifespanUserAction": 300,
            "accessCodeLifespanLogin": 1800
        }'
    
    echo "Realm ${REALM_NAME} criado com sucesso!"
fi

# Criar roles
echo "Criando roles no realm..."
ROLES=("ADMIN" "USER" "MANAGER")

for ROLE in "${ROLES[@]}"; do
    echo "Criando role ${ROLE}..."
    curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/roles" \
        -H "Authorization: Bearer ${TOKEN}" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "'${ROLE}'",
            "description": "Role '${ROLE}' para VintiPay"
        }'
done

echo "Roles criadas com sucesso!"

# Verificar se o client já existe
echo "Verificando se o client ${CLIENT_ID} já existe..."
CLIENT_EXISTS=$(curl -s "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients" \
    -H "Authorization: Bearer ${TOKEN}" | jq -r '.[] | select(.clientId=="'${CLIENT_ID}'") | .id')

if [ -n "$CLIENT_EXISTS" ]; then
    echo "Client ${CLIENT_ID} já existe com ID: ${CLIENT_EXISTS}"
else
    echo "Criando client ${CLIENT_ID}..."
    CLIENT_RESPONSE=$(curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients" \
        -H "Authorization: Bearer ${TOKEN}" \
        -H "Content-Type: application/json" \
        -d '{
            "clientId": "'${CLIENT_ID}'",
            "name": "VintiPay Application",
            "description": "Cliente OAuth2 para aplicação VintiPay",
            "enabled": true,
            "clientAuthenticatorType": "client-secret",
            "secret": "'${CLIENT_SECRET}'",
            "redirectUris": [
                "https://localhost:8443/*",
                "https://localhost:8443/login/oauth2/code/keycloak",
                "http://localhost:8443/*",
                "http://localhost:8443/login/oauth2/code/keycloak"
            ],
            "webOrigins": [
                "https://localhost:8443",
                "http://localhost:8443"
            ],
            "publicClient": false,
            "protocol": "openid-connect",
            "directAccessGrantsEnabled": true,
            "serviceAccountsEnabled": true,
            "authorizationServicesEnabled": false,
            "standardFlowEnabled": true,
            "implicitFlowEnabled": false,
            "directAccessGrantsEnabled": true,
            "attributes": {
                "access.token.lifespan": "300",
                "client.session.idle.timeout": "1800",
                "client.session.max.lifespan": "36000"
            },
            "fullScopeAllowed": true
        }')
    
    echo "Client ${CLIENT_ID} criado com sucesso!"
fi

# Obter ID do client para adicionar mappers
echo "Obtendo ID do client ${CLIENT_ID}..."
CLIENT_ID_UUID=$(curl -s "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients" \
    -H "Authorization: Bearer ${TOKEN}" | jq -r '.[] | select(.clientId=="'${CLIENT_ID}'") | .id')

if [ -n "$CLIENT_ID_UUID" ] && [ "$CLIENT_ID_UUID" != "null" ]; then
    echo "Configurando mappers de roles no client..."
    
    # Mapper para realm roles
    echo "Criando mapper para realm roles..."
    curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients/${CLIENT_ID_UUID}/protocol-mappers/models" \
        -H "Authorization: Bearer ${TOKEN}" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "realm roles",
            "protocol": "openid-connect",
            "protocolMapper": "oidc-usermodel-realm-role-mapper",
            "consentRequired": false,
            "config": {
                "multivalued": "true",
                "userinfo.token.claim": "true",
                "id.token.claim": "true",
                "access.token.claim": "true",
                "claim.name": "realm_access.roles",
                "jsonType.label": "String"
            }
        }'
    
    # Mapper para client roles
    echo "Criando mapper para client roles..."
    curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients/${CLIENT_ID_UUID}/protocol-mappers/models" \
        -H "Authorization: Bearer ${TOKEN}" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "client roles",
            "protocol": "openid-connect",
            "protocolMapper": "oidc-usermodel-client-role-mapper",
            "consentRequired": false,
            "config": {
                "multivalued": "true",
                "userinfo.token.claim": "true",
                "id.token.claim": "true",
                "access.token.claim": "true",
                "claim.name": "resource_access.${client_id}.roles",
                "jsonType.label": "String"
            }
        }'
    
    # Mapper para audience
    echo "Criando mapper para audience..."
    curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients/${CLIENT_ID_UUID}/protocol-mappers/models" \
        -H "Authorization: Bearer ${TOKEN}" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "audience",
            "protocol": "openid-connect",
            "protocolMapper": "oidc-audience-mapper",
            "consentRequired": false,
            "config": {
                "included.client.audience": "'${CLIENT_ID}'",
                "id.token.claim": "true",
                "access.token.claim": "true"
            }
        }'
    
    echo "Mappers de roles configurados com sucesso!"
fi

# Criar usuário admin de teste
echo "Criando usuário admin de teste..."
curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "admin@vintipay.cv",
        "email": "admin@vintipay.cv",
        "firstName": "Admin",
        "lastName": "VintiPay",
        "enabled": true,
        "emailVerified": true,
        "credentials": [{
            "type": "password",
            "value": "admin123",
            "temporary": false
        }]
    }'

# Obter ID do usuário admin
ADMIN_USER_ID=$(curl -s "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users?username=admin@vintipay.cv" \
    -H "Authorization: Bearer ${TOKEN}" | jq -r '.[0].id')

if [ -n "$ADMIN_USER_ID" ] && [ "$ADMIN_USER_ID" != "null" ]; then
    echo "Atribuindo role ADMIN ao usuário admin..."
    
    # Obter ID da role ADMIN
    ADMIN_ROLE_ID=$(curl -s "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/roles/ADMIN" \
        -H "Authorization: Bearer ${TOKEN}" | jq -r '.id')
    
    curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users/${ADMIN_USER_ID}/role-mappings/realm" \
        -H "Authorization: Bearer ${TOKEN}" \
        -H "Content-Type: application/json" \
        -d '[{
            "id": "'${ADMIN_ROLE_ID}'",
            "name": "ADMIN"
        }]'

    # Atribuir role realm-admin do client realm-management para permitir acesso à Admin API
    REALM_MGMT_CLIENT_ID=$(curl -s "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients?clientId=realm-management" \
        -H "Authorization: Bearer ${TOKEN}" | jq -r '.[0].id')
    
    if [ -n "$REALM_MGMT_CLIENT_ID" ] && [ "$REALM_MGMT_CLIENT_ID" != "null" ]; then
        REALM_ADMIN_ROLE_ID=$(curl -s "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients/${REALM_MGMT_CLIENT_ID}/roles/realm-admin" \
            -H "Authorization: Bearer ${TOKEN}" | jq -r '.id')
        
        if [ -n "$REALM_ADMIN_ROLE_ID" ] && [ "$REALM_ADMIN_ROLE_ID" != "null" ]; then
            echo "Atribuindo role realm-admin do realm-management ao usuário admin..."
            curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users/${ADMIN_USER_ID}/role-mappings/clients/${REALM_MGMT_CLIENT_ID}" \
                -H "Authorization: Bearer ${TOKEN}" \
                -H "Content-Type: application/json" \
                -d '[{
                    "id": "'${REALM_ADMIN_ROLE_ID}'",
                    "name": "realm-admin"
                }]' 
        fi
    fi
    
    echo "Role ADMIN atribuída com sucesso!"
fi

echo ""
echo "=========================================="
echo "Configuração do Keycloak concluída!"
echo "=========================================="
echo "Realm: ${REALM_NAME}"
echo "Client ID: ${CLIENT_ID}"
echo "Client Secret: ${CLIENT_SECRET}"
echo "Keycloak Admin Console: ${KEYCLOAK_URL}"
echo "Usuário Admin: admin / admin"
echo "Usuário Teste: admin@vintipay.cv / admin123"
echo "=========================================="
