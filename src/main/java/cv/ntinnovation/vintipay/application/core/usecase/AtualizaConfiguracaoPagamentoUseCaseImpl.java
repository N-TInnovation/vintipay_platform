package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.port.in.AtualizaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtualizaConfiguracaoPagamentoUseCaseImpl implements AtualizaConfiguracaoPagamentoUseCase {

    private final SalvaConfiguracaoPagamentoPort salvaConfiguracaoPagamentoPort;
    private final BuscaConfiguracaoPagamentoPort buscaConfiguracaoPagamentoPort;
    private final FingerPrintService fingerPrintService;

    @Override
    @Transactional
    public ConfiguracaoPagamento atualizar(ConfiguracaoPagamento configuracaoPagamento) {
        // Busca a configuração existente
        ConfiguracaoPagamento configuracaoExistente = buscaConfiguracaoPagamentoPort.buscarPorId(configuracaoPagamento.getId()).orElse(null);
        if (configuracaoExistente == null) {
            throw new RuntimeException("Configuração não encontrada");
        }

        // Criptografar o POS Aut Code com AES256
        String posAutCodeOriginal = configuracaoPagamento.getPosAutCode();
        String posAutCodeCriptografado = fingerPrintService.encryptAES256(posAutCodeOriginal);
        log.info("POS Aut Code criptografado com AES256 durante atualização");

        // Atualiza apenas os campos permitidos
        configuracaoExistente.setPosId(configuracaoPagamento.getPosId());
        configuracaoExistente.setMerchantId(configuracaoPagamento.getMerchantId());
        configuracaoExistente.setPosAutCode(posAutCodeCriptografado); // Salvar o valor criptografado
        configuracaoExistente.setReturnUrl(configuracaoPagamento.getReturnUrl());
        configuracaoExistente.setIsActive(configuracaoPagamento.getIsActive());
        
        // Atualiza os novos campos de informações da loja
        configuracaoExistente.setNomeLoja(configuracaoPagamento.getNomeLoja());
        configuracaoExistente.setUrlLoja(configuracaoPagamento.getUrlLoja());
        configuracaoExistente.setTelefone(configuracaoPagamento.getTelefone());
        configuracaoExistente.setEmail(configuracaoPagamento.getEmail());
        
        // Atualiza campos adicionais
        configuracaoExistente.setAmbiente(configuracaoPagamento.getAmbiente());
        configuracaoExistente.setWebhookUrl(configuracaoPagamento.getWebhookUrl());
        configuracaoExistente.setWebhookSecret(configuracaoPagamento.getWebhookSecret());
        configuracaoExistente.setAtivo(configuracaoPagamento.getAtivo());
        configuracaoExistente.setWebhookAtivo(configuracaoPagamento.getWebhookAtivo());
        configuracaoExistente.setReturnAtivo(configuracaoPagamento.getReturnAtivo());
        configuracaoExistente.setObservacoes(configuracaoPagamento.getObservacoes());
        
        // Atualiza as configurações de aplicação (integrações)
        if (configuracaoPagamento.getConfiguracaoAplicacoes() != null) {
            log.info("Atualizando configurações de aplicação. Total: {}", 
                    configuracaoPagamento.getConfiguracaoAplicacoes().size());
            
            // Importante: para cada aplicação, garantir que o relacionamento bidirecional esteja configurado
            for (var app : configuracaoPagamento.getConfiguracaoAplicacoes()) {
                if (app.getConfiguracaoPagamento() == null) {
                    log.info("Configurando relação com ConfiguracaoPagamento para aplicação: {}", app.getDescription());
                    app.setConfiguracaoPagamento(configuracaoExistente);
                }
            }
            
            configuracaoExistente.setConfiguracaoAplicacoes(configuracaoPagamento.getConfiguracaoAplicacoes());
        }
        
        // Não atualizar o API Secret se não for fornecido um novo
        if (configuracaoPagamento.getApiSecret() != null && !configuracaoPagamento.getApiSecret().isEmpty()) {
            log.info("Atualizando API Secret para a configuração ID: {}", configuracaoPagamento.getId());
            configuracaoExistente.setApiSecret(configuracaoPagamento.getApiSecret());
        } else {
            log.info("Mantendo API Secret existente para a configuração ID: {}", configuracaoPagamento.getId());
        }
        
        // Garante que as datas estejam preenchidas
        if (configuracaoExistente.getCreatedAt() == null) {
            configuracaoExistente.setCreatedAt(LocalDateTime.now());
        }
        configuracaoExistente.setUpdatedAt(LocalDateTime.now());

        ConfiguracaoPagamento configuracaoAtualizada = salvaConfiguracaoPagamentoPort.salvar(configuracaoExistente);
        
        // Retorna a configuração com o posAutCode original (não criptografado)
        // para exibição na interface
        configuracaoAtualizada.setPosAutCode(posAutCodeOriginal);
        
        return configuracaoAtualizada;
    }

    @Override
    @Transactional
    public void ativar(Long id) {
        ConfiguracaoPagamento configuracao = buscaConfiguracaoPagamentoPort.buscarPorId(id).orElse(null);
        if (configuracao == null) {
            throw new RuntimeException("Configuração não encontrada");
        }
        configuracao.setIsActive(true);
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
        configuracao.setUpdatedAt(LocalDateTime.now());
        salvaConfiguracaoPagamentoPort.salvar(configuracao);
    }
}
