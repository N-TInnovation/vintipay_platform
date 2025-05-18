package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.IntegracaoEnum;
import cv.ntinnovation.vintipay.application.port.in.ProcessaIntegracaoUseCase;
import cv.ntinnovation.vintipay.application.port.out.ConfiguracaoIntegracoesPort;
import cv.ntinnovation.vintipay.application.port.out.VendusServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cv.ntinnovation.vintipay.application.core.domain.enums.IntegracaoEnum.VENDUS;

/**
 * Implementação do caso de uso para processamento de recibos Vendus
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessaVendusReceiptUseCaseImpl implements ProcessaIntegracaoUseCase<IntegracaoEnum> {

    private final VendusServicePort vendusService;
    private final ConfiguracaoIntegracoesPort configuracaoIntegracoesPort;

    @Override
    public IntegracaoEnum processar(ConfiguracaoPagamento configuracao, Pagamento pagamento) {

        if (pagamento == null) {
            log.error("Não é possível processar recibo Vendus para pagamento nulo");
            return null;
        }

        log.info("Processando recibo Vendus para pagamento ID: {}", pagamento.getId());

        try {
            iniciarServicosAplicacao(configuracao);

            VENDUS.processar(configuracao, pagamento);

            return VENDUS;
        } catch (Exception e) {
            log.error("Erro ao processar recibo Vendus", e);
            return null;
        }
    }

    private void iniciarServicosAplicacao(ConfiguracaoPagamento configuracao) {
        //noinspection unchecked
        var aplicacoes = configuracaoIntegracoesPort.buscarTodas(configuracao.getUsuarioId());
        for (var aplicacao : aplicacoes) {
            if (aplicacao.getIntegracaoEnum().equals(VENDUS)) {
                VENDUS.getIntegracao().setAplicacao(aplicacao);
                VENDUS.getIntegracao().setService(vendusService);
            }
        }
        configuracao.setConfiguracaoAplicacoes(aplicacoes);

    }
}
