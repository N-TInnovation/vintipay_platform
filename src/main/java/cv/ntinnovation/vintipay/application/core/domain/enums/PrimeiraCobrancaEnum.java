package cv.ntinnovation.vintipay.application.core.domain.enums;

/**
 * Enum para definir quando ocorre a primeira cobrança em produtos recorrentes
 */
public enum PrimeiraCobrancaEnum {
    IMEDIATA("Imediata", "Primeira cobrança ocorre já na subscrição"),
    APOS_PERIODO("Após o período", "Primeira cobrança ocorre após o primeiro período completo");
    
    private final String descricao;
    private final String detalhes;
    
    PrimeiraCobrancaEnum(String descricao, String detalhes) {
        this.descricao = descricao;
        this.detalhes = detalhes;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public String getDetalhes() {
        return detalhes;
    }
}
