/**
 * Carrega os códigos de telefone dos países para o dropdown
 */
function loadPhoneCodes() {
    const phoneCodeSelect = document.getElementById("phoneCode");
    if (!phoneCodeSelect) return;
    
    // Adicionar alguns códigos comuns como fallback inicial
    const fallbackCodes = [
        { code: "238", name: "Cabo Verde 🇨🇻" },
        { code: "351", name: "Portugal 🇵🇹" },
        { code: "1", name: "EUA/Canadá 🇺🇸" },
        { code: "44", name: "Reino Unido 🇬🇧" },
        { code: "55", name: "Brasil 🇧🇷" },
        { code: "34", name: "Espanha 🇪🇸" },
        { code: "33", name: "França 🇫🇷" },
        { code: "49", name: "Alemanha 🇩🇪" },
        { code: "39", name: "Itália 🇮🇹" },
        { code: "31", name: "Holanda 🇳🇱" }
    ];
    
    // Inicialmente, preencher com os códigos de fallback
    phoneCodeSelect.innerHTML = "<option value=\"\">Código</option>";
    fallbackCodes.forEach(code => {
        const option = document.createElement("option");
        option.value = code.code;
        option.textContent = "+" + code.code + " " + code.name.split(" ")[1];
        phoneCodeSelect.appendChild(option);
    });
    
    // Definir Cabo Verde como padrão
    phoneCodeSelect.value = "238";
    
    // Tentar carregar os códigos da API
    fetch("/api/location/countries")
        .then(response => {
            if (!response.ok) {
                throw new Error("Erro ao carregar códigos de telefone");
            }
            return response.json();
        })
        .then(countries => {
            // Se a API retornar com sucesso, substituir os códigos de fallback
            if (countries && countries.length > 0) {
                // Ordenar países pelo código de telefone
                countries.sort((a, b) => {
                    const phoneCodeA = a.phonecode ? parseInt(a.phonecode) : 999;
                    const phoneCodeB = b.phonecode ? parseInt(b.phonecode) : 999;
                    return phoneCodeA - phoneCodeB;
                });
                
                phoneCodeSelect.innerHTML = "<option value=\"\">Código</option>";
                
                countries.forEach(country => {
                    if (country.phonecode) {
                        const option = document.createElement("option");
                        option.value = country.phonecode;
                        option.textContent = "+" + country.phonecode + " " + (country.emoji || "");
                        phoneCodeSelect.appendChild(option);
                    }
                });
                
                // Definir Cabo Verde como padrão (código 238)
                phoneCodeSelect.value = "238";
            }
        })
        .catch(error => {
            console.error("Erro ao carregar códigos de telefone:", error);
            // Já temos os códigos de fallback, então não precisamos fazer nada aqui
        });
}

// Executar quando o DOM estiver carregado
document.addEventListener('DOMContentLoaded', function() {
    loadPhoneCodes();
});
