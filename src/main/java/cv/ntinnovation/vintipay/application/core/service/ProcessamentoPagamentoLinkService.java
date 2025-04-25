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
    public SessaoPagamento criarSessaoPagamento(String codigoLink) {
        log.info("Criando sessão de pagamento para o link: {}", codigoLink);
        
        // Busca o link de pagamento e registra o uso
        LinkPagamento linkPagamento = gerenciaLinkPagamentoUseCase.registrarUsoLinkPagamento(codigoLink);
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
                linkPagamento.getUrlRetornoSucesso(),
                linkPagamento.getUrlRetornoCancelamento()
        );
        
        // Associa a sessão à intenção de pagamento
        sessaoPagamento.setIntencaoPagamento(intencaoPagamento);
        sessaoPagamento = salvaSessaoPagamento.salvar(sessaoPagamento);
        
        return sessaoPagamento;
    }

    @Override
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

    @Override
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
    
    private String gerarReferenciaUnica() {
        return "LINK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
