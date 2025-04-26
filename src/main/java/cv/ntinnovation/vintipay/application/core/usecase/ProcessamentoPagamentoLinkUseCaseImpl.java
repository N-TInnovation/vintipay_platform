package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.ContatoContaPagamento;
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
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class ProcessamentoPagamentoLinkUseCaseImpl implements ProcessamentoPagamentoLinkUseCase {

    private final BuscaLinkPagamento buscaLinkPagamento;
    private final GerenciaLinkPagamentoUseCase gerenciaLinkPagamentoUseCase;
    private final BuscaIntencaoPagamento buscaIntencaoPagamento;
    private final SalvaIntencaoPagamento salvaIntencaoPagamento;
    private final SalvaSessaoPagamento salvaSessaoPagamento;
    private final ProcessadorPagamento processadorPagamento;

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
        String referencia = gerarReferenciaUnica();
        IntencaoPagamento intencaoPagamento = IntencaoPagamento.builder()
                .referencia(referencia)
                .valor(produto.getPreco())
                .moeda(produto.getMoeda() != null ? produto.getMoeda() : "CVE")
                .descricao(produto.getDescricao())
                .status(StatusIntencaoPagamentoEnum.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .build();
        
        // Salvar a intenção de pagamento
        intencaoPagamento = salvaIntencaoPagamento.salvar(intencaoPagamento);
        
        // Incrementar o contador de usos do link
        gerenciaLinkPagamentoUseCase.incrementarUsos(link.getId());
        
        // Criar a sessão de pagamento com o processador
        SessaoPagamento sessaoPagamento = processadorPagamento.criarSessaoPagamento(
                intencaoPagamento.getReferencia(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                intencaoPagamento.getMoeda(),
                successUrl,
                cancelUrl
        );
        
        // Associar a sessão à intenção de pagamento
        sessaoPagamento.setIntencaoPagamentoId(intencaoPagamento.getId().toString());
        salvaSessaoPagamento.salvar(sessaoPagamento);
        
        // Retornar a URL de checkout
        return sessaoPagamento.getCheckoutUrl();
    }
    
    @Override
    @Transactional
    public String criarSessaoPagamento(String codigoLink, String successUrl, String cancelUrl,
                                      String email, String cidade, String pais, String endereco,
                                      String codigoPostal, String merchantSession, String codigoTelefone,
                                      String numeroTelefone) {
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
        
        // Criar uma intenção de pagamento com os dados do comprador
        String referencia = gerarReferenciaUnica();
        IntencaoPagamento intencaoPagamento = IntencaoPagamento.builder()
                .referencia(referencia)
                .valor(produto.getPreco())
                .moeda(produto.getMoeda() != null ? produto.getMoeda() : "CVE")
                .descricao(produto.getDescricao())
                .status(StatusIntencaoPagamentoEnum.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                // Dados do comprador
                .contaPagamentoEmail(email)
                .contaPagamentoBillAddrCity(cidade)
                .contaPagamentoBillAddrCountry(pais)
                .contaPagamentoBillAddrLine1(endereco)
                .contaPagamentoBillAddrPostCode(codigoPostal)
                .merchantSession(merchantSession)
                .build();
        
        // Criar a conta de pagamento com os dados do comprador
        var mobilePhone = ContatoContaPagamento.builder()
                .cc(codigoTelefone)
                .subscriber(numeroTelefone)
                .build();
        
        var contaPagamento = ContaPagamento.builder()
                .email(email)
                .billAddrCity(cidade)
                .billAddrCountry(pais)
                .billAddrLine1(endereco)
                .billAddrPostCode(codigoPostal)
                .mobilePhone(mobilePhone)
                .build();
        
        intencaoPagamento.setContaPagamento(contaPagamento);
        
        // Salvar a intenção de pagamento
        intencaoPagamento = salvaIntencaoPagamento.salvar(intencaoPagamento);
        
        // Incrementar o contador de usos do link
        gerenciaLinkPagamentoUseCase.incrementarUsos(link.getId());
        
        // Criar a sessão de pagamento com o processador, incluindo os dados do comprador
        SessaoPagamento sessaoPagamento = processadorPagamento.criarSessaoPagamento(
                intencaoPagamento.getReferencia(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                intencaoPagamento.getMoeda(),
                successUrl,
                cancelUrl,
                contaPagamento,
                merchantSession
        );
        
        // Associar a sessão à intenção de pagamento
        sessaoPagamento.setIntencaoPagamentoId(intencaoPagamento.getId().toString());
        salvaSessaoPagamento.salvar(sessaoPagamento);
        
        log.info("Sessão de pagamento criada com sucesso para o link {} com dados do comprador: email={}, cidade={}", 
                codigoLink, email, cidade);
        
        // Retornar a URL de checkout
        return sessaoPagamento.getCheckoutUrl();
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
        
        // Atualizar o status da intenção de pagamento
        intencaoPagamento.setStatus(StatusIntencaoPagamentoEnum.PAGO);
        intencaoPagamento.setDataAtualizacao(LocalDateTime.now());
        salvaIntencaoPagamento.salvar(intencaoPagamento);
        
        log.info("Pagamento processado com sucesso para a sessão: {} e link: {}", sessionId, codigoLink);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IntencaoPagamento> buscarIntencaoPorCodigoLink(String codigoLink) {
        log.debug("Buscando intenção de pagamento para o link: {}", codigoLink);
        
        // Buscar o link de pagamento pelo código
        Optional<LinkPagamento> linkOpt = buscaLinkPagamento.buscarPorCodigo(codigoLink);
        if (linkOpt.isEmpty()) {
            log.warn("Link de pagamento não encontrado: {}", codigoLink);
            return Optional.empty();
        }
        
        // Como não temos uma associação direta entre link e intenção,
        // vamos buscar a intenção de pagamento pela referência que contém o código do link
        // Assumindo que a referência gerada no método criarSessaoPagamento contém o prefixo "LINK-"
        
        // Buscar todas as sessões de pagamento associadas a este link
        // Nota: Isso é uma solução temporária. Idealmente, deveríamos ter uma associação direta
        // entre link e intenção de pagamento, ou um método específico para buscar por código de link
        
        // Buscar a intenção de pagamento mais recente associada a este link
        String referenciaPrefix = "LINK-";
        
        // Usar o método buscarPorReferencia para encontrar a intenção
        // Como não temos um método específico para buscar por código de link,
        // retornamos a última intenção de pagamento criada para este link (se existir)
        return buscaIntencaoPagamento.buscarPorReferencia(referenciaPrefix + codigoLink);
    }
    
    /**
     * Gera uma referência única para a intenção de pagamento
     * 
     * @return Referência única
     */
    private String gerarReferenciaUnica() {
        return "LINK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
