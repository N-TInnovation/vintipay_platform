package cv.ntinnovation.vintipay.application.core.domain.enums;

/**
 * Enum que representa as categorias de produtos disponíveis no sistema.
 */
public enum CategoriaProdutoEnum {
    ALIMENTACAO("Alimentação"),
    VESTUARIO("Vestuário"),
    ELETRONICOS("Eletrônicos"),
    SERVICOS("Serviços"),
    ASSINATURAS("Assinaturas"),
    EVENTOS("Eventos"),
    EDUCACAO("Educação"),
    SAUDE("Saúde"),
    BELEZA("Beleza"),
    OUTROS("Outros");
    
    private final String descricao;
    
    CategoriaProdutoEnum(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}
