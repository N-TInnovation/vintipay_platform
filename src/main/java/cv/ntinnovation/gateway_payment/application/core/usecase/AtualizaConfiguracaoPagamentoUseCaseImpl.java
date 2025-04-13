package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.port.in.AtualizaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaConfiguracaoPagamentoPort;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AtualizaConfiguracaoPagamentoUseCaseImpl implements AtualizaConfiguracaoPagamentoUseCase {

    private final SalvaConfiguracaoPagamentoPort salvaConfiguracaoPagamentoPort;
    private final BuscaConfiguracaoPagamentoPort buscaConfiguracaoPagamentoPort;

    @Override
    @Transactional
    public ConfiguracaoPagamento atualizar(ConfiguracaoPagamento configuracaoPagamento) {
        // Busca a configuração existente
        ConfiguracaoPagamento configuracaoExistente = buscaConfiguracaoPagamentoPort.buscarPorId(configuracaoPagamento.getId()).orElse(null);
        if (configuracaoExistente == null) {
            throw new RuntimeException("Configuração não encontrada");
        }

        // Atualiza apenas os campos permitidos
        configuracaoExistente.setPosId(configuracaoPagamento.getPosId());
        configuracaoExistente.setMerchantId(configuracaoPagamento.getMerchantId());
        configuracaoExistente.setTerminalId(configuracaoPagamento.getTerminalId());
        configuracaoExistente.setPosAutCode(configuracaoPagamento.getPosAutCode());
        configuracaoExistente.setReturnUrl(configuracaoPagamento.getReturnUrl());
        configuracaoExistente.setIsActive(configuracaoPagamento.getIsActive());
        
        // Garante que as datas estejam preenchidas
        if (configuracaoExistente.getCreatedAt() == null) {
            configuracaoExistente.setCreatedAt(LocalDateTime.now());
        }
        configuracaoExistente.setUpdatedAt(LocalDateTime.now());

        return salvaConfiguracaoPagamentoPort.salvar(configuracaoExistente);
    }

    @Override
    @Transactional
    public void ativar(Long id) {
        ConfiguracaoPagamento configuracao = buscaConfiguracaoPagamentoPort.buscarPorId(id).orElse(null);
        if (configuracao == null) {
            throw new RuntimeException("Configuração não encontrada");
        }

        configuracao.setIsActive(true);
        if (configuracao.getCreatedAt() == null) {
            configuracao.setCreatedAt(LocalDateTime.now());
        }
        configuracao.setUpdatedAt(LocalDateTime.now());
        salvaConfiguracaoPagamentoPort.salvar(configuracao);
    }

    @Override
    @Transactional
    public void desativar(Long id) {
        ConfiguracaoPagamento configuracao = buscaConfiguracaoPagamentoPort.buscarPorId(id).orElse(null);
        if (configuracao == null) {
            throw new RuntimeException("Configuração não encontrada");
        }

        configuracao.setIsActive(false);
        if (configuracao.getCreatedAt() == null) {
            configuracao.setCreatedAt(LocalDateTime.now());
        }
        configuracao.setUpdatedAt(LocalDateTime.now());
        salvaConfiguracaoPagamentoPort.salvar(configuracao);
    }
}
