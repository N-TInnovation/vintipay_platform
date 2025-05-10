package cv.ntinnovation.vintipay.adapter.out.db.util;

import cv.ntinnovation.vintipay.adapter.out.db.entity.ConfiguracaoIntegracoesEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.ConfiguracaoPagamentoEntity;

/**
 * Utilitário para manter relacionamentos bidirecionais entre entidades.
 */
public class RelacionamentoBidirecionalUtil {

    /**
     * Configura o relacionamento bidirecional entre ConfiguracaoPagamentoEntity e ConfiguracaoAplicacaoEntity.
     * Este método deve ser chamado após converter as entidades de domínio para entidades JPA.
     *
     * @param pagamento Entidade de configuração de pagamento
     * @param aplicacao Entidade de configuração de aplicação
     */
    public static void configurarRelacionamentoPagamentoAplicacao(
            ConfiguracaoPagamentoEntity pagamento, 
            ConfiguracaoIntegracoesEntity aplicacao) {
        
        if (pagamento != null && aplicacao != null) {
            // Define o pagamento na aplicação
            aplicacao.setConfiguracaoPagamento(pagamento);

        }
    }
}
