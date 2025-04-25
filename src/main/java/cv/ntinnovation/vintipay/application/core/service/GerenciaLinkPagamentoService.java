package cv.ntinnovation.vintipay.application.core.service;

import cv.ntinnovation.vintipay.application.core.domain.entity.LinkPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusLinkPagamentoEnum;
import cv.ntinnovation.vintipay.application.port.in.GerenciaLinkPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaLinkPagamento;
import cv.ntinnovation.vintipay.application.port.out.BuscaProduto;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import cv.ntinnovation.vintipay.application.port.out.SalvaLinkPagamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GerenciaLinkPagamentoService implements GerenciaLinkPagamentoUseCase {

    private final SalvaLinkPagamento salvaLinkPagamento;
    private final BuscaLinkPagamento buscaLinkPagamento;
    private final BuscaProduto buscaProduto;
    private final BuscaUsuario buscaUsuario;

    @Override
    @Transactional
    public LinkPagamento criarLinkPagamento(Long produtoId, Long proprietarioId, String titulo, String descricao,
                                           LocalDateTime dataExpiracao, Integer quantidadeUsos,
                                           String urlRetornoSucesso, String urlRetornoCancelamento) {
        log.info("Criando link de pagamento para produto: {}, proprietário: {}", produtoId, proprietarioId);
        
        Produto produto = buscaProduto.buscarPorId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        
        Usuario proprietario = buscaUsuario.buscarPorId(proprietarioId)
                .orElseThrow(() -> new IllegalArgumentException("Proprietário não encontrado"));
        
        // Verifica se o usuário é o proprietário do produto
        if (!produto.getProprietario().getId().equals(proprietarioId)) {
            throw new IllegalArgumentException("Usuário não tem permissão para criar link para este produto");
        }
        
        // Gera um código único para o link
        String codigo = gerarCodigoUnico();
        
        LinkPagamento linkPagamento = LinkPagamento.builder()
                .codigo(codigo)
                .titulo(titulo != null ? titulo : produto.getNome())
                .descricao(descricao != null ? descricao : produto.getDescricao())
                .status(StatusLinkPagamentoEnum.ATIVO)
                .dataCriacao(LocalDateTime.now())
                .dataExpiracao(dataExpiracao)
                .quantidadeUsos(quantidadeUsos)
                .usos(0)
                .produto(produto)
                .proprietario(proprietario)
                .urlRetornoSucesso(urlRetornoSucesso)
                .urlRetornoCancelamento(urlRetornoCancelamento)
                .build();
        
        return salvaLinkPagamento.salvar(linkPagamento);
    }

    @Override
    @Transactional
    public LinkPagamento atualizarLinkPagamento(String codigo, Long proprietarioId, String titulo, String descricao,
                                              StatusLinkPagamentoEnum status, LocalDateTime dataExpiracao,
                                              Integer quantidadeUsos, String urlRetornoSucesso, 
                                              String urlRetornoCancelamento) {
        log.info("Atualizando link de pagamento: {}, proprietário: {}", codigo, proprietarioId);
        
        LinkPagamento linkPagamento = buscaLinkPagamento.buscarPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Link de pagamento não encontrado"));
        
        // Verifica se o usuário é o proprietário do link
        if (!linkPagamento.getProprietario().getId().equals(proprietarioId)) {
            throw new IllegalArgumentException("Usuário não tem permissão para atualizar este link");
        }
        
        if (titulo != null) {
            linkPagamento.setTitulo(titulo);
        }
        
        if (descricao != null) {
            linkPagamento.setDescricao(descricao);
        }
        
        if (status != null) {
            linkPagamento.setStatus(status);
        }
        
        if (dataExpiracao != null) {
            linkPagamento.setDataExpiracao(dataExpiracao);
        }
        
        if (quantidadeUsos != null) {
            linkPagamento.setQuantidadeUsos(quantidadeUsos);
        }
        
        if (urlRetornoSucesso != null) {
            linkPagamento.setUrlRetornoSucesso(urlRetornoSucesso);
        }
        
        if (urlRetornoCancelamento != null) {
            linkPagamento.setUrlRetornoCancelamento(urlRetornoCancelamento);
        }
        
        return salvaLinkPagamento.salvar(linkPagamento);
    }

    @Override
    @Transactional
    public void desativarLinkPagamento(String codigo, Long proprietarioId) {
        log.info("Desativando link de pagamento: {}, proprietário: {}", codigo, proprietarioId);
        
        LinkPagamento linkPagamento = buscaLinkPagamento.buscarPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Link de pagamento não encontrado"));
        
        // Verifica se o usuário é o proprietário do link
        if (!linkPagamento.getProprietario().getId().equals(proprietarioId)) {
            throw new IllegalArgumentException("Usuário não tem permissão para desativar este link");
        }
        
        linkPagamento.setStatus(StatusLinkPagamentoEnum.INATIVO);
        salvaLinkPagamento.salvar(linkPagamento);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LinkPagamento> buscarLinkPagamentoPorCodigo(String codigo) {
        log.info("Buscando link de pagamento por código: {}", codigo);
        return buscaLinkPagamento.buscarPorCodigo(codigo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LinkPagamento> listarLinksPagamentoPorProprietario(Long proprietarioId) {
        log.info("Listando links de pagamento do proprietário: {}", proprietarioId);
        return buscaLinkPagamento.buscarPorProprietarioId(proprietarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LinkPagamento> listarLinksPagamentoPorProduto(Long produtoId, Long proprietarioId) {
        log.info("Listando links de pagamento do produto: {}, proprietário: {}", produtoId, proprietarioId);
        
        Produto produto = buscaProduto.buscarPorId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        
        // Verifica se o usuário é o proprietário do produto
        if (!produto.getProprietario().getId().equals(proprietarioId)) {
            throw new IllegalArgumentException("Usuário não tem permissão para listar links deste produto");
        }
        
        return buscaLinkPagamento.buscarPorProdutoId(produtoId);
    }

    @Override
    @Transactional
    public LinkPagamento registrarUsoLinkPagamento(String codigo) {
        log.info("Registrando uso do link de pagamento: {}", codigo);
        
        LinkPagamento linkPagamento = buscaLinkPagamento.buscarPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Link de pagamento não encontrado"));
        
        // Verifica se o link está ativo
        if (linkPagamento.getStatus() != StatusLinkPagamentoEnum.ATIVO) {
            throw new IllegalStateException("Link de pagamento não está ativo");
        }
        
        // Verifica se o link não expirou
        if (linkPagamento.getDataExpiracao() != null && 
            linkPagamento.getDataExpiracao().isBefore(LocalDateTime.now())) {
            linkPagamento.setStatus(StatusLinkPagamentoEnum.EXPIRADO);
            salvaLinkPagamento.salvar(linkPagamento);
            throw new IllegalStateException("Link de pagamento expirado");
        }
        
        // Verifica se o link não atingiu o limite de usos
        if (linkPagamento.getQuantidadeUsos() != null && 
            linkPagamento.getUsos() >= linkPagamento.getQuantidadeUsos()) {
            linkPagamento.setStatus(StatusLinkPagamentoEnum.LIMITE_ATINGIDO);
            salvaLinkPagamento.salvar(linkPagamento);
            throw new IllegalStateException("Link de pagamento atingiu o limite de usos");
        }
        
        // Incrementa o contador de usos
        linkPagamento.setUsos(linkPagamento.getUsos() + 1);
        
        // Se atingiu o limite após este uso, atualiza o status
        if (linkPagamento.getQuantidadeUsos() != null && 
            linkPagamento.getUsos() >= linkPagamento.getQuantidadeUsos()) {
            linkPagamento.setStatus(StatusLinkPagamentoEnum.LIMITE_ATINGIDO);
        }
        
        return salvaLinkPagamento.salvar(linkPagamento);
    }
    
    private String gerarCodigoUnico() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean verificarPropriedadeLink(Long linkId, Long proprietarioId) {
        log.info("Verificando propriedade do link: {}, proprietário: {}", linkId, proprietarioId);
        
        Optional<LinkPagamento> linkOpt = buscaLinkPagamento.buscarPorId(linkId);
        if (linkOpt.isEmpty()) {
            return false;
        }
        
        LinkPagamento link = linkOpt.get();
        return link.getProprietario().getId().equals(proprietarioId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<LinkPagamento> buscarLinkPorId(Long id) {
        log.info("Buscando link de pagamento por ID: {}", id);
        return buscaLinkPagamento.buscarPorId(id);
    }
    
    @Override
    @Transactional
    public boolean excluirLinkPagamento(Long id) {
        log.info("Excluindo link de pagamento: {}", id);
        
        Optional<LinkPagamento> linkOpt = buscaLinkPagamento.buscarPorId(id);
        if (linkOpt.isEmpty()) {
            return false;
        }
        
        LinkPagamento link = linkOpt.get();
        link.setStatus(StatusLinkPagamentoEnum.INATIVO);
        salvaLinkPagamento.salvar(link);
        
        return true;
    }
}
