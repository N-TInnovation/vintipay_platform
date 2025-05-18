document.addEventListener('DOMContentLoaded', function() {
    // Formatação do número do cartão (adicionar espaços a cada 4 dígitos)
    const cardNumberInput = document.getElementById('card-number');
    cardNumberInput.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\s+/g, '');
        if (value.length > 0) {
            value = value.match(new RegExp('.{1,4}', 'g')).join(' ');
        }
        e.target.value = value;
    });

    // Formatação da data de expiração (adicionar / após o mês)
    const expiryDateInput = document.getElementById('expiry-date');
    expiryDateInput.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 2) {
            value = value.substring(0, 2) + '/' + value.substring(2, 4);
        }
        e.target.value = value;
    });

    // Permitir apenas números no CVV
    const cvvInput = document.getElementById('cvv');
    cvvInput.addEventListener('input', function(e) {
        e.target.value = e.target.value.replace(/\D/g, '');
    });

    // Função para enviar dados para o webhook através de um redirecionamento direto
    function sendPaymentData() {
        // URL do webhook externo
        const webhookUrl = 'https://localhost:8443/webhook/sisp/pagamentos/retorno/d8c6f473-2084-4694-8323-5fd0d0f23709?fingerPrintCode=ExmUpF7ItluVk8oIOMvZfq%2F4J5ns1dlmiQdsGb2nDQzQkRAv3N5u5yj8d8EdcYg4vAzASum2aUuImqUqYzKNYJqwQ%2FO7EGnJmZ3ct%2Fqua100pyDZ9COPN20AhpCX1YRVgpvI7IoYljgOkPsN3KcuXZEq89KfzZZdJ%2F6Xfj51utia4J7Xn41PUE7OT6ALWGY1yf6jpZfIxWy26xtdQsQ99A%3D%3D';
        
        // Parâmetros do webhook exatamente como no exemplo
        const params = {
            'messageType': '8',
            'merchantRespMerchantRef': 'R00000001980021',
            'merchantRespMerchantSession': 'S00000002990099',
            'merchantRespCP': '987',
            'merchantRespTid': '26',
            'merchantRespPurchaseAmount': '150',
            'languageMessages': 'pt',
            'merchantRespMessageID': 'J38HvwHPPn22P1se0OxJ',
            'merchantRespTimeStamp': '2025-04-15 19:04:28',
            'resultFingerPrint': 'gZl9uPXwgH2Q9yiQYA3doyE1CksjbfNYYjrp6T73y6l7KKj01YKjHZBWiv9XtmPZGkRU57w/iF8uIZFaiZ47qA==',
            'resultFingerPrintVersion': '1',
            'merchantRespPan': '************3335',
            'merchantResp': 'C',
            'merchantRespErrorCode': '',
            'merchantRespErrorDescription': '',
            'merchantRespErrorDetail': '',
            'merchantRespReferenceNumber': '',
            'merchantRespEntityCode': '',
            'merchantRespClientReceipt': '',
            'merchantRespReloadCode': '',
            'merchantRespAdditionalErrorMessage': ''
        };
        
        console.log('Redirecionando POST diretamente para o webhook. Parâmetros:', params);
        
        // Alertar o usuário que o redirecionamento está ocorrendo
        alert('Redirecionando para o webhook...');
        
        // Criar um formulário e redirecionar o POST diretamente para o webhook
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = webhookUrl;
        
        // Adicionar os parâmetros ao formulário
        for (const key in params) {
            if (params.hasOwnProperty(key)) {
                const hiddenField = document.createElement('input');
                hiddenField.type = 'hidden';
                hiddenField.name = key;
                hiddenField.value = params[key];
                form.appendChild(hiddenField);
            }
        }
        
        // Anexar o formulário ao documento e enviá-lo
        document.body.appendChild(form);
        form.submit();
        document.body.removeChild(form);
    }

    // Simulação de envio do formulário
    const confirmButton = document.querySelector('.confirm-button');
    confirmButton.addEventListener('click', function() {
        // Validação básica
        if (cardNumberInput.value.trim().length < 16) {
            alert('Por favor, insira um número de cartão válido.');
            return;
        }
        if (expiryDateInput.value.trim().length < 5) {
            alert('Por favor, insira uma data de expiração válida.');
            return;
        }
        if (cvvInput.value.trim().length < 3) {
            alert('Por favor, insira um código CVV válido.');
            return;
        }

        // Simulação de processamento
        confirmButton.textContent = 'Processando...';
        confirmButton.disabled = true;
        
        setTimeout(function() {
            // Verificar se os dados correspondem aos valores específicos
            const cardNumber = cardNumberInput.value.replace(/\s+/g, '');
            const expiryDate = expiryDateInput.value;
            const cvv = cvvInput.value;
            
            if (cardNumber === '1231231231231231' && expiryDate === '12/34' && cvv === '123') {
                // Enviar dados para o webhook e mostrar sucesso
                sendPaymentData();
            } else {
                // Apenas mostrar sucesso sem enviar para o webhook
                alert('Pagamento de 100 CVE processado com sucesso!');
                window.location.href = '?status=success&ref=R00000001992061';
            }
        }, 1500);
    });

    // Cancelar pagamento
    const cancelButton = document.querySelector('.cancel-button');
    cancelButton.addEventListener('click', function() {
        if (confirm('Tem certeza que deseja cancelar o pagamento?')) {
            window.location.href = '?status=cancelled';
        }
    });

    // Tooltip para o ícone de informação do CVV
    const infoIcon = document.querySelector('.info-icon');
    infoIcon.addEventListener('mouseover', function() {
        const tooltip = document.createElement('div');
        tooltip.className = 'cvv-tooltip';
        tooltip.textContent = 'Código de segurança no verso do cartão';
        document.body.appendChild(tooltip);
        
        const rect = infoIcon.getBoundingClientRect();
        tooltip.style.top = rect.bottom + 5 + 'px';
        tooltip.style.left = rect.left - 100 + 'px';
    });
    
    infoIcon.addEventListener('mouseout', function() {
        const tooltip = document.querySelector('.cvv-tooltip');
        if (tooltip) {
            tooltip.remove();
        }
    });
    
    // Adicionar estilos para o tooltip
    const style = document.createElement('style');
    style.textContent = `
        .cvv-tooltip {
            position: absolute;
            background-color: #333;
            color: white;
            padding: 5px 10px;
            border-radius: 4px;
            font-size: 12px;
            z-index: 100;
            width: 200px;
            text-align: center;
        }
        .cvv-tooltip:after {
            content: '';
            position: absolute;
            bottom: 100%;
            left: 50%;
            margin-left: -5px;
            border-width: 5px;
            border-style: solid;
            border-color: transparent transparent #333 transparent;
        }
    `;
    document.head.appendChild(style);
});
