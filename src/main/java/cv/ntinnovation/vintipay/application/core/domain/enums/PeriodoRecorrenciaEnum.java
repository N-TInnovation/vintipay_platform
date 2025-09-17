package cv.ntinnovation.vintipay.application.core.domain.enums;

/**
 * Enum para representar os diferentes períodos de recorrência disponíveis para produtos
 */
public enum PeriodoRecorrenciaEnum {
    DIARIO("Diário"),
    SEMANAL("Semanal"),
    QUINZENAL("Quinzenal"),
    MENSAL("Mensal"),
    BIMESTRAL("Bimestral"),
    TRIMESTRAL("Trimestral"),
    SEMESTRAL("Semestral"),
    ANUAL("Anual");
    
    private final String descricao;
    
    PeriodoRecorrenciaEnum(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
