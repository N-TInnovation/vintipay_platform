package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.LinkPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusLinkPagamentoEnum;
import cv.ntinnovation.vintipay.application.port.in.GerenciaLinkPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaLinkPagamento;
import cv.ntinnovation.vintipay.application.port.out.BuscaProduto;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuarioPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaLinkPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GerenciaLinkPagamentoUseCaseImpl implements GerenciaLinkPagamentoUseCase {

    private final SalvaLinkPagamento salvaLinkPagamento;
    private final BuscaLinkPagamento buscaLinkPagamento;
    private final BuscaProduto buscaProduto;
    private final BuscaUsuarioPort buscaUsuarioPort;

    @Override
    @Transactional
    public LinkPagamento criarLinkPagamento(Long produtoId, Long proprietarioId, 
                                          String titulo, String descricao,
                                          LocalDateTime dataExpiracao, Integer quantidadeUsos,
                                          String urlRetornoSucesso, String urlRetornoCancelamento) {
        // Validações
        if (produtoId == null) {
            throw new IllegalArgumentException("ID do produto é obrigatório");
        }
        
        if (proprietarioId == null) {
            throw new IllegalArgumentException("ID do proprietário é obrigatório");
        }
        
        // Buscar o produto
        Optional<Produto> produtoOpt = buscaProduto.buscarPorId(produtoId);
        if (produtoOpt.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado");
        }
        
        Produto produto = produtoOpt.get();
        
        // Verificar se o produto pertence ao proprietário
        if (!produto.getProprietarioId().equals(proprietarioId)) {
            throw new IllegalArgumentException("Produto não pertence ao usuário informado");
        }
        
        // Buscar o usuário proprietário
        Optional<Usuario> proprietarioOpt = buscaUsuarioPort.buscarPorUsername(proprietarioId.toString());
        if (proprietarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário proprietário não encontrado");
        }
        
        // Gerar código único para o link
        String codigo = gerarCodigoUnico();
        
        // Criar o link de pagamento
        LinkPagamento linkPagamento = LinkPagamento.builder()
                .codigo(codigo)
                .titulo(titulo)
                .descricao(descricao)
                .status(StatusLinkPagamentoEnum.ATIVO)
                .dataCriacao(LocalDateTime.now())
                .dataExpiracao(dataExpiracao)
                .quantidadeUsos(quantidadeUsos)
                .usos(0)
                .produto(produto)
                .produtoId(produtoId)
                .proprietario(proprietarioOpt.get())
                .proprietarioId(proprietarioId)
                .urlRetornoSucesso(urlRetornoSucesso)
                .urlRetornoCancelamento(urlRetornoCancelamento)
                .build();
        
        // Salvar o link de pagamento
        return salvaLinkPagamento.salvar(linkPagamento);
    }

    @Override
    @Transactional
    public LinkPagamento atualizarLinkPagamento(Long linkId, String titulo, String descricao,
                                              LocalDateTime dataExpiracao, Integer quantidadeUsos,
                                              StatusLinkPagamentoEnum status,
                                              String urlRetornoSucesso, String urlRetornoCancelamento) {
        // Buscar o link de pagamento
        Optional<LinkPagamento> linkOpt = buscaLinkPagamento.buscarPorId(linkId);
        if (linkOpt.isEmpty()) {
            throw new IllegalArgumentException("Link de pagamento não encontrado");
        }
        
        LinkPagamento link = linkOpt.get();
        
        // Atualizar os campos
        if (titulo != null) {
            link.setTitulo(titulo);
        }
        
        if (descricao != null) {
            link.setDescricao(descricao);
        }
        
        if (dataExpiracao != null) {
            link.setDataExpiracao(dataExpiracao);
        }
        
        if (quantidadeUsos != null) {
            link.setQuantidadeUsos(quantidadeUsos);
        }
        
        if (status != null) {
            link.setStatus(status);
        }
        
        if (urlRetornoSucesso != null) {
            link.setUrlRetornoSucesso(urlRetornoSucesso);
        }
        
        if (urlRetornoCancelamento != null) {
            link.setUrlRetornoCancelamento(urlRetornoCancelamento);
        }
        
        // Salvar o link atualizado
        return salvaLinkPagamento.salvar(link);
    }

    @Override
    @Transactional
    public boolean alterarStatusLink(Long linkId, StatusLinkPagamentoEnum status) {
        // Buscar o link de pagamento
        Optional<LinkPagamento> linkOpt = buscaLinkPagamento.buscarPorId(linkId);
        if (linkOpt.isEmpty()) {
            return false;
        }
        
        LinkPagamento link = linkOpt.get();
        
        // Atualizar o status
        link.setStatus(status);
        
        // Salvar o link atualizado
        salvaLinkPagamento.salvar(link);
        
        return true;
    }

    @Override
    @Transactional
    public void desativarLinkPagamento(String codigo, Long proprietarioId) {
        // Buscar o link de pagamento pelo código
        Optional<LinkPagamento> linkOpt = buscaLinkPagamento.buscarPorCodigo(codigo);
        if (linkOpt.isEmpty()) {
            throw new IllegalArgumentException("Link de pagamento não encontrado");
        }
        
        LinkPagamento link = linkOpt.get();
        
        // Verificar se o usuário é o proprietário do link
        if (!link.getProprietarioId().equals(proprietarioId)) {
            throw new IllegalArgumentException("Usuário não tem permissão para desativar este link");
        }
        
        // Desativar o link
        link.setStatus(StatusLinkPagamentoEnum.INATIVO);
        
        // Salvar o link atualizado
        salvaLinkPagamento.salvar(link);
    }

    @Override
    @Transactional
    public boolean incrementarUsos(Long linkId) {
        // Buscar o link de pagamento
        Optional<LinkPagamento> linkOpt = buscaLinkPagamento.buscarPorId(linkId);
        if (linkOpt.isEmpty()) {
            return false;
        }
        
        LinkPagamento link = linkOpt.get();
        
        // Incrementar o contador de usos
        link.setUsos(link.getUsos() + 1);
        
        // Verificar se atingiu o limite de usos
        if (link.getQuantidadeUsos() != null && link.getUsos() >= link.getQuantidadeUsos()) {
            link.setStatus(StatusLinkPagamentoEnum.LIMITE_ATINGIDO);
        }
        
        // Salvar o link atualizado
        salvaLinkPagamento.salvar(link);
        
        return true;
    }

    @Override
    @Transactional
    public boolean excluirLinkPagamento(Long linkId) {
        // Buscar o link de pagamento
        Optional<LinkPagamento> linkOpt = buscaLinkPagamento.buscarPorId(linkId);
        if (linkOpt.isEmpty()) {
            return false;
        }
        
        LinkPagamento link = linkOpt.get();
        
        // Marcar como inativo (soft delete)
        link.setStatus(StatusLinkPagamentoEnum.INATIVO);
        
        // Salvar o link atualizado
        salvaLinkPagamento.salvar(link);
        
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LinkPagamento> listarLinksPorProprietario(Long proprietarioId) {
        return buscaLinkPagamento.buscarPorProprietarioId(proprietarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LinkPagamento> listarLinksPorProduto(Long produtoId) {
        return buscaLinkPagamento.buscarPorProdutoId(produtoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LinkPagamento> buscarLinkPorId(Long linkId) {
        return buscaLinkPagamento.buscarPorId(linkId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LinkPagamento> buscarLinkPorCodigo(String codigo) {
        return buscaLinkPagamento.buscarPorCodigo(codigo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarPropriedadeLink(Long linkId, Long proprietarioId) {
        return buscaLinkPagamento.existePorIdEProprietarioId(linkId, proprietarioId);
    }
    
    /**
     * Gera um código único para o link de pagamento
     * 
     * @return Código único
     */
    private String gerarCodigoUnico() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
