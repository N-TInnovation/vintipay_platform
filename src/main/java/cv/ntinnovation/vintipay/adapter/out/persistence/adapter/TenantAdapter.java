package cv.ntinnovation.vintipay.adapter.out.persistence.adapter;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.TenantEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.TenantMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.TenantRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Tenant;
import cv.ntinnovation.vintipay.application.port.out.GerenciaTenantPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TenantAdapter implements GerenciaTenantPort {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    @Override
    public Tenant salvar(Tenant tenant) {
        TenantEntity entity = tenantMapper.toEntity(tenant);
        entity = tenantRepository.save(entity);
        return tenantMapper.toDomain(entity);
    }

    @Override
    public void excluir(Long id) {
        tenantRepository.deleteById(id);
    }

    @Override
    public Optional<Tenant> buscarPorId(Long id) {
        return tenantRepository.findById(id)
                .map(tenantMapper::toDomain);
    }

    @Override
    public Optional<Tenant> buscarPorSlug(String slug) {
        return tenantRepository.findBySlug(slug)
                .map(tenantMapper::toDomain);
    }

    @Override
    public Optional<Tenant> buscarPorCodigo(String codigo) {
        return tenantRepository.findByCodigo(codigo)
                .map(tenantMapper::toDomain);
    }

    @Override
    public List<Tenant> buscarPorSlugIn(List<String> slugs) {
        return tenantRepository.findBySlugIn(slugs).stream()
                .map(tenantMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existePorSlug(String slug) {
        return tenantRepository.existsBySlug(slug);
    }

    @Override
    public List<Tenant> listarTodos() {
        return tenantRepository.findAll().stream()
                .map(tenantMapper::toDomain)
                .collect(Collectors.toList());
    }
}
