package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.mapper.PlanoMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.PlanoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Plano;
import cv.ntinnovation.vintipay.application.port.out.BuscaPlanoPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaPlanoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PlanoAdapter implements BuscaPlanoPort, SalvaPlanoPort {

    private final PlanoRepository planoRepository;
    private final PlanoMapper planoMapper;

    @Override
    public List<Plano> buscarTodosAtivos() {
        return planoRepository.findByAtivoTrue()
                .stream()
                .map(planoMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Plano> buscarTodos() {
        return planoRepository.findAll()
                .stream()
                .map(planoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Plano> buscarPorId(Long id) {
        return planoRepository.findById(id)
                .map(planoMapper::toDomain);
    }

    @Override
    public Optional<Plano> buscarPorNome(String nome) {
        return planoRepository.findByNome(nome)
                .map(planoMapper::toDomain);
    }

    @Override
    public Optional<Plano> buscarPorStripeProductId(String stripeProductId) {
        return planoRepository.findByStripeProductId(stripeProductId)
                .map(planoMapper::toDomain);
    }

    @Override
    public Plano salvar(Plano plano) {
        var entity = planoMapper.toEntity(plano);
        var savedEntity = planoRepository.save(entity);
        return planoMapper.toDomain(savedEntity);
    }
}
