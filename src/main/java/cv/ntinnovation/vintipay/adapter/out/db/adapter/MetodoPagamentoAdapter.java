package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.mapper.MetodoPagamentoEntityMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.MetodoPagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.MetodoPagamento;
import cv.ntinnovation.vintipay.application.port.out.BuscaMetodoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaMetodoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MetodoPagamentoAdapter implements BuscaMetodoPagamentoPort, SalvaMetodoPagamentoPort {

    private final MetodoPagamentoRepository repository;
    private final MetodoPagamentoEntityMapper mapper;

    @Override
    public List<MetodoPagamento> buscarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<MetodoPagamento> buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<MetodoPagamento> buscarPorNome(String nome) {
        return repository.findByNameIgnoreCase(nome)
                .map(mapper::toDomain);
    }

    @Override
    public MetodoPagamento salvar(MetodoPagamento metodoPagamento) {
        return mapper.toDomain(
                repository.save(
                        mapper.toEntity(metodoPagamento)
                )
        );
    }
}
