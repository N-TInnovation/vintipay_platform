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

    const form = document.getElementById('payment-form');

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
            // Simulação de sucesso
            confirmButton.textContent = 'Confirmar pagamento';
            confirmButton.disabled = false;
            form.submit();
        }, 2000);

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
