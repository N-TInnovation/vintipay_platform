package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.ProdutoMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.ProdutoRepository;
import cv.ntinnovation.vintipay.adapter.out.db.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.port.out.BuscaProduto;
import cv.ntinnovation.vintipay.application.port.out.SalvaProduto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProdutoAdapter implements SalvaProduto, BuscaProduto {

    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoMapper produtoMapper;

    @Override
    public Produto salvar(Produto produto) {
        var entity = produtoMapper.toEntity(produto);
        
        // Se o proprietário não foi carregado, mas temos o ID, vamos carregar
        if (entity.getProprietario() == null && produto.getProprietarioId() != null) {
            Optional<UsuarioEntity> proprietarioOpt = usuarioRepository.findById(produto.getProprietarioId());
            proprietarioOpt.ifPresent(entity::setProprietario);
        }
        
        var savedEntity = produtoRepository.save(entity);
        return produtoMapper.toDomain(savedEntity);
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
    public boolean existePorId(Long id) {
        return produtoRepository.existsById(id);
    }

    @Override
    public boolean existePorIdEProprietarioId(Long id, Long proprietarioId) {
        return produtoRepository.existsByIdAndProprietarioId(id, proprietarioId);
    }
    
    @Override
    public List<Produto> buscarTodosAtivos() {
        return produtoRepository.findByAtivoTrue()
                .stream()
                .map(produtoMapper::toDomain)
                .collect(Collectors.toList());
    }
}
