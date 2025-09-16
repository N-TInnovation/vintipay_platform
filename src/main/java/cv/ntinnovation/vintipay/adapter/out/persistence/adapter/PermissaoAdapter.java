package cv.ntinnovation.vintipay.adapter.out.persistence.adapter;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PermissaoEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.PermissaoMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.PermissaoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Permissao;
import cv.ntinnovation.vintipay.application.port.out.GerenciaPermissaoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissaoAdapter implements GerenciaPermissaoPort {

    private final PermissaoRepository permissaoRepository;
    private final PermissaoMapper permissaoMapper;

    @Override
    public Permissao salvar(Permissao permissao) {
        PermissaoEntity entity = permissaoMapper.toEntity(permissao);
        entity = permissaoRepository.save(entity);
        return permissaoMapper.toDomain(entity);
    }

    @Override
    public void excluir(Long id) {
        permissaoRepository.deleteById(id);
    }

    @Override
    public Optional<Permissao> buscarPorId(Long id) {
        return permissaoRepository.findById(id)
                .map(permissaoMapper::toDomain);
    }

    @Override
    public Optional<Permissao> buscarPorCodigo(String codigo) {
        return permissaoRepository.findByCodigo(codigo)
                .map(permissaoMapper::toDomain);
    }

    @Override
    public List<Permissao> listarTodas() {
        return permissaoRepository.findAll().stream()
                .map(permissaoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return permissaoRepository.existsByCodigo(codigo);
    }
}
