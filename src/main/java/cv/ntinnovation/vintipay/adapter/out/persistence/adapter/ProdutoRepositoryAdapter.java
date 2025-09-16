package cv.ntinnovation.vintipay.adapter.out.persistence.adapter;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.ProdutoEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.ProdutoMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.ProdutoRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.port.out.BuscaProduto;
import cv.ntinnovation.vintipay.application.port.out.SalvaProduto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Component
@RequiredArgsConstructor
public class ProdutoRepositoryAdapter implements SalvaProduto, BuscaProduto {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;
    private final UsuarioRepository usuarioRepository;

    @Override
    public Produto salvar(Produto produto) {
        ProdutoEntity produtoEntity = produtoMapper.toEntity(produto);
        
        // Se o proprietário não foi carregado, mas temos o ID, vamos carregar
        if (produtoEntity.getProprietario() == null && produto.getProprietarioId() != null) {
            Optional<UsuarioEntity> proprietarioOpt = usuarioRepository.findById(produto.getProprietarioId());
            proprietarioOpt.ifPresent(produtoEntity::setProprietario);
        }
        
        produtoEntity = produtoRepository.save(produtoEntity);
        return produtoMapper.toDomain(produtoEntity);
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .map(produtoMapper::toDomain);
    }

    @Override
    public List<Produto> buscarPorProprietarioId(Long proprietarioId) {
        return produtoRepository.findByProprietarioIdOrderByDataCriacaoDesc(proprietarioId)
                .stream()
                .map(produtoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Produto> buscarTodosAtivos() {
        return produtoRepository.findByAtivoTrue()
                .stream()
                .map(produtoMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existePorId(Long id) {
        return produtoRepository.existsById(id);
    }
    
    @Override
    public boolean existePorIdEProprietarioId(Long id, Long proprietarioId) {
        return produtoRepository.existsByIdAndProprietarioId(id, proprietarioId);
    }
}
