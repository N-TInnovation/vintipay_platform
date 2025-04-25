package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.LinkPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusLinkPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.port.in.CriaIntencaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.GerenciaLinkPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.ProcessamentoPagamentoLinkUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaLinkPagamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessamentoPagamentoLinkUseCaseImpl implements ProcessamentoPagamentoLinkUseCase {

    private final BuscaLinkPagamento buscaLinkPagamento;
    private final GerenciaLinkPagamentoUseCase gerenciaLinkPagamentoUseCase;
    private final CriaIntencaoPagamentoUseCase criaIntencaoPagamentoUseCase;

    @Override
    @Transactional
    public String criarSessaoPagamento(String codigoLink, String successUrl, String cancelUrl) {
        // Buscar o link de pagamento pelo código
        Optional<LinkPagamento> linkOpt = buscaLinkPagamento.buscarPorCodigo(codigoLink);
        if (linkOpt.isEmpty()) {
            throw new IllegalArgumentException("Link de pagamento não encontrado");
        }
        
        LinkPagamento link = linkOpt.get();
        
        // Verificar se o link está disponível
        if (!link.isDisponivel()) {
            throw new IllegalStateException("Link de pagamento não está disponível");
        }
        
        Produto produto = link.getProduto();
        
        // Verificar se o produto está ativo
        if (!produto.isAtivo()) {
            throw new IllegalStateException("Produto não está disponível para compra");
        }
        
        // Criar uma intenção de pagamento
        IntencaoPagamento intencaoPagamento = IntencaoPagamento.builder()
                .amount(produto.getPreco())
                .currency(produto.getMoeda())
                .reference(UUID.randomUUID().toString())
                .status(StatusPagamentoEnum.PENDENTE)
                .createdAt(LocalDateTime.now())
                .contaPagamento(link.getProprietario().getContaPagamento())
                .build();
        
        // Salvar a intenção de pagamento
        IntencaoPagamento intencaoPagamentoCriada = criaIntencaoPagamentoUseCase.criar(intencaoPagamento);
        
        // Incrementar o contador de usos do link
        gerenciaLinkPagamentoUseCase.incrementarUsos(link.getId());
        
        // Retornar a URL de checkout
        // Aqui você deve integrar com o serviço SISP para criar a sessão de pagamento
        // e retornar a URL de checkout
        
        // Exemplo simplificado (você deve substituir por uma integração real):
        String checkoutUrl = "https://checkout.sisp.cv/?reference=" + intencaoPagamentoCriada.getReference() +
                "&amount=" + intencaoPagamentoCriada.getAmount() +
                "&currency=" + intencaoPagamentoCriada.getCurrency() +
                "&success_url=" + successUrl +
                "&cancel_url=" + cancelUrl;
        
        return checkoutUrl;
    }

    @Override
    @Transactional
    public boolean processarPagamentoSucesso(String sessionId, String codigoLink) {
        // Buscar o link de pagamento pelo código
        Optional<LinkPagamento> linkOpt = buscaLinkPagamento.buscarPorCodigo(codigoLink);
        if (linkOpt.isEmpty()) {
            log.error("Link de pagamento não encontrado: {}", codigoLink);
            return false;
        }
        
        LinkPagamento link = linkOpt.get();
        
        // Aqui você deve implementar a lógica para verificar o status do pagamento
        // junto ao serviço SISP usando o sessionId
        
        // Se o pagamento foi bem-sucedido, você pode atualizar o status da intenção de pagamento
        // e realizar outras ações necessárias
        
        log.info("Pagamento processado com sucesso para o link: {}", codigoLink);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IntencaoPagamento> buscarIntencaoPorCodigoLink(String codigoLink) {
        // Esta implementação depende de como você está associando as intenções de pagamento
        // aos links. Você pode precisar adicionar um campo na entidade LinkPagamento para
        // armazenar a referência da intenção de pagamento, ou criar uma entidade de associação.
        
        // Exemplo simplificado (você deve adaptar à sua estrutura de dados):
        Optional<LinkPagamento> linkOpt = buscaLinkPagamento.buscarPorCodigo(codigoLink);
        if (linkOpt.isEmpty()) {
            return Optional.empty();
        }
        
        // Aqui você deve buscar a intenção de pagamento associada ao link
        // Esta é uma implementação fictícia, você deve adaptá-la à sua estrutura
        return Optional.empty();
    }
}
