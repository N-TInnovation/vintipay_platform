package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.port.in.AtualizaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AtualizaConfiguracaoPagamentoUseCaseImpl implements AtualizaConfiguracaoPagamentoUseCase {

    private final SalvaConfiguracaoPagamentoPort salvaConfiguracaoPagamento;
    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoUseCase;

    @Override
    @Transactional
    public ConfiguracaoPagamento atualizar(ConfiguracaoPagamento configuracao) {
        if (configuracao.getId() == null) {
            throw new IllegalArgumentException("ID da configuração não pode ser nulo para atualização");
        }
        
        // Verifica se a configuração existe
        buscaConfiguracaoPagamentoUseCase.buscarPorId(configuracao.getId());
        
        return salvaConfiguracaoPagamento.salvar(configuracao);
    }

    @Override
    @Transactional
    public void ativar(Long id) {
        ConfiguracaoPagamento configuracao = buscaConfiguracaoPagamentoUseCase.buscarPorId(id);
        configuracao.setIsActive(true);
        salvaConfiguracaoPagamento.salvar(configuracao);
    }

    @Override
    @Transactional
    public void desativar(Long id) {
        ConfiguracaoPagamento configuracao = buscaConfiguracaoPagamentoUseCase.buscarPorId(id);
        configuracao.setIsActive(false);
        salvaConfiguracaoPagamento.salvar(configuracao);
    }
}
