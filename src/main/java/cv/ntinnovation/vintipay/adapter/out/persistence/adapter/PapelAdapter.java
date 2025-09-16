package cv.ntinnovation.vintipay.adapter.out.persistence.adapter;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PapelEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.PapelMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.PapelRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Papel;
import cv.ntinnovation.vintipay.application.port.out.GerenciaPapelPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PapelAdapter implements GerenciaPapelPort {

    private final PapelRepository papelRepository;
    private final PapelMapper papelMapper;

    @Override
    public Papel salvar(Papel papel) {
        PapelEntity entity = papelMapper.toEntity(papel);
        entity = papelRepository.save(entity);
        return papelMapper.toDomain(entity);
    }

    @Override
    public void excluir(Long id) {
        papelRepository.deleteById(id);
    }

    @Override
    public Optional<Papel> buscarPorId(Long id) {
        return papelRepository.findById(id)
                .map(papelMapper::toDomain);
    }

    @Override
    public Optional<Papel> buscarPorCodigo(String codigo) {
        return papelRepository.findByCodigo(codigo)
                .map(papelMapper::toDomain);
    }

    @Override
    public Optional<Papel> buscarPapelPadrao() {
        return papelRepository.findByPadraoTrue()
                .map(papelMapper::toDomain);
    }

    @Override
    public List<Papel> listarTodos() {
        return papelRepository.findAll().stream()
                .map(papelMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return papelRepository.existsByCodigo(codigo);
    }
}
