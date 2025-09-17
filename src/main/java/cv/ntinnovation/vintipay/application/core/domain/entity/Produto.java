package cv.ntinnovation.vintipay.application.core.domain.entity;

import cv.ntinnovation.vintipay.application.core.domain.enums.CategoriaProdutoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.PeriodoRecorrenciaEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.PrimeiraCobrancaEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String imagem;
    private String codigoSku;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    
    // Relacionamentos
    private Usuario proprietario;
    private Long proprietarioId; // Para facilitar a criação
    
    // Campos adicionais
    private Integer estoque; // Opcional, pode ser null para produtos digitais ou serviços
    private CategoriaProdutoEnum categoria;
    private String moeda; // Por padrão, CVE
    
    // Campos para cobranças recorrentes
    private boolean recorrente;
    private PeriodoRecorrenciaEnum periodoRecorrencia;
    private Integer duracaoRecorrencia; // Número de ciclos (null = ilimitado)
    private String descricaoRecorrencia; // Descrição amigável da recorrência
    
    // Novas configurações para cobranças recorrentes
    private PrimeiraCobrancaEnum primeiraCobranca; // Quando ocorre a primeira cobrança
    private List<DescontoCiclo> descontosPorCiclo = new ArrayList<>(); // Lista de descontos por ciclo
}
