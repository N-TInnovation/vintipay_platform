package cv.ntinnovation.vintipay.application.core.service;

import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.LinkPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusIntencaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.port.in.GerenciaLinkPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.ProcessamentoPagamentoLinkUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaIntencaoPagamento;
import cv.ntinnovation.vintipay.application.port.out.BuscaLinkPagamento;
import cv.ntinnovation.vintipay.application.port.out.ProcessadorPagamento;
import cv.ntinnovation.vintipay.application.port.out.SalvaIntencaoPagamento;
import cv.ntinnovation.vintipay.application.port.out.SalvaSessaoPagamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessamentoPagamentoLinkService implements ProcessamentoPagamentoLinkUseCase {

    private final BuscaLinkPagamento buscaLinkPagamento;
    private final GerenciaLinkPagamentoUseCase gerenciaLinkPagamentoUseCase;
    private final SalvaIntencaoPagamento salvaIntencaoPagamento;
    private final BuscaIntencaoPagamento buscaIntencaoPagamento;
    private final SalvaSessaoPagamento salvaSessaoPagamento;
    private final ProcessadorPagamento processadorPagamento;

    @Override
    @Transactional
    public String criarSessaoPagamento(String codigoLink, String successUrl, String cancelUrl) {
        log.info("Criando sessão de pagamento para o link: {} com URLs de retorno personalizadas", codigoLink);
        
        // Busca o link de pagamento pelo código
        LinkPagamento linkPagamento = gerenciaLinkPagamentoUseCase.buscarLinkPorCodigo(codigoLink)
                .orElseThrow(() -> new IllegalArgumentException("Link de pagamento não encontrado: " + codigoLink));
        
        // Incrementa o contador de usos do link
        gerenciaLinkPagamentoUseCase.incrementarUsos(linkPagamento.getId());
        
        Produto produto = linkPagamento.getProduto();
        
        // Cria a intenção de pagamento
        String referencia = gerarReferenciaUnica();
        IntencaoPagamento intencaoPagamento = IntencaoPagamento.builder()
                .referencia(referencia)
                .valor(produto.getPreco())
                .moeda(produto.getMoeda() != null ? produto.getMoeda() : "CVE")
                .descricao(produto.getDescricao())
                .status(StatusIntencaoPagamentoEnum.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .build();
        
        intencaoPagamento = salvaIntencaoPagamento.salvar(intencaoPagamento);
        
        // Cria a sessão de pagamento
        SessaoPagamento sessaoPagamento = processadorPagamento.criarSessaoPagamento(
                intencaoPagamento.getReferencia(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                intencaoPagamento.getMoeda(),
                successUrl,
                cancelUrl
        );
        
        // Associa a sessão à intenção de pagamento
        sessaoPagamento.setIntencaoPagamentoId(intencaoPagamento.getId().toString());
        sessaoPagamento = salvaSessaoPagamento.salvar(sessaoPagamento);
        
        return sessaoPagamento.getCheckoutUrl();
    }

    @Override
    @Transactional
    public boolean processarPagamentoSucesso(String sessionId, String codigoLink) {
        log.info("Processando pagamento com sucesso para a sessão: {} e link: {}", sessionId, codigoLink);
        
        // Buscar a sessão de pagamento e a intenção associada
        Optional<SessaoPagamento> sessaoOpt = processadorPagamento.buscarSessaoPorId(sessionId);
        if (sessaoOpt.isEmpty()) {
            log.error("Sessão de pagamento não encontrada: {}", sessionId);
            return false;
        }
        
        SessaoPagamento sessao = sessaoOpt.get();
        IntencaoPagamento intencaoPagamento = buscaIntencaoPagamento.buscarPorId(sessao.getIntencaoPagamentoId());
        
        if (intencaoPagamento == null) {
            log.error("Intenção de pagamento não encontrada para a sessão: {}", sessionId);
            return false;
        }
        
        // Atualiza o status da intenção de pagamento
        intencaoPagamento.setStatus(StatusIntencaoPagamentoEnum.PAGO);
        intencaoPagamento.setDataAtualizacao(LocalDateTime.now());
        salvaIntencaoPagamento.salvar(intencaoPagamento);
        
        log.info("Pagamento processado com sucesso para a sessão: {} e link: {}", sessionId, codigoLink);
        return true;
    }

    /**
     * Processa o pagamento bem-sucedido a partir da referência
     * 
     * @param referencia Referência da intenção de pagamento
     */
    @Transactional
    public void processarPagamentoSucesso(String referencia) {
        log.info("Processando pagamento com sucesso para a referência: {}", referencia);
        
        IntencaoPagamento intencaoPagamento = buscaIntencaoPagamento.buscarPorReferencia(referencia)
                .orElseThrow(() -> new IllegalArgumentException("Intenção de pagamento não encontrada"));
        
        // Atualiza o status da intenção de pagamento
        intencaoPagamento.setStatus(StatusIntencaoPagamentoEnum.PAGO);
        intencaoPagamento.setDataAtualizacao(LocalDateTime.now());
        salvaIntencaoPagamento.salvar(intencaoPagamento);
        
        // Aqui poderia ser implementada lógica adicional como:
        // - Enviar e-mail de confirmação
        // - Atualizar estoque do produto
        // - Registrar a venda em um histórico
        // - Gerar nota fiscal, etc.
    }

    /**
     * Processa o cancelamento de pagamento a partir da referência
     * 
     * @param referencia Referência da intenção de pagamento
     */
    @Transactional
    public void processarPagamentoCancelado(String referencia) {
        log.info("Processando cancelamento de pagamento para a referência: {}", referencia);
        
        IntencaoPagamento intencaoPagamento = buscaIntencaoPagamento.buscarPorReferencia(referencia)
                .orElseThrow(() -> new IllegalArgumentException("Intenção de pagamento não encontrada"));
        
        // Atualiza o status da intenção de pagamento
        intencaoPagamento.setStatus(StatusIntencaoPagamentoEnum.CANCELADO);
        intencaoPagamento.setDataAtualizacao(LocalDateTime.now());
        salvaIntencaoPagamento.salvar(intencaoPagamento);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<IntencaoPagamento> buscarIntencaoPorCodigoLink(String codigoLink) {
        log.info("Buscando intenção de pagamento para o link: {}", codigoLink);
        
        // Buscar o link de pagamento pelo código
        Optional<LinkPagamento> linkOpt = buscaLinkPagamento.buscarPorCodigo(codigoLink);
        if (linkOpt.isEmpty()) {
            log.warn("Link de pagamento não encontrado: {}", codigoLink);
            return Optional.empty();
        }
        
        // Como não temos uma associação direta entre link e intenção de pagamento no modelo atual,
        // precisamos implementar uma lógica para buscar a intenção mais recente associada ao link.
        // Esta é uma implementação simplificada que pode ser melhorada com uma associação direta
        // ou uma tabela de relacionamento entre links e intenções.
        
        // Por enquanto, retornamos vazio, mas em uma implementação real,
        // buscaríamos a intenção de pagamento mais recente associada ao link
        return Optional.empty();
    }
    
    private String gerarReferenciaUnica() {
        return "LINK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
