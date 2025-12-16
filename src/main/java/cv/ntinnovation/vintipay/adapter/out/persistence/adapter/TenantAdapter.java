package cv.ntinnovation.vintipay.adapter.out.persistence.adapter;

import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.VerifikaAdapter;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.TenantEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.TenantMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.TenantRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Tenant;
import cv.ntinnovation.vintipay.application.port.out.GerenciaTenantPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantAdapter implements GerenciaTenantPort {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final VerifikaAdapter verifikaAdapter;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public Tenant salvar(Tenant tenant) {
        TenantEntity entity = tenantMapper.toEntity(tenant);
        entity = tenantRepository.save(entity);
        
        // Associar tenant ao usuário logado do Keycloak
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String id = null;
            String email = null;

            if (authentication.getPrincipal() instanceof CustomOAuth2User customUser) {
                id = customUser.getId();
                email = customUser.getEmail();
            }

            if (id != null) {
                Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByEmail(email);
                if (usuarioOpt.isPresent()) {
                    UsuarioEntity usuario = usuarioOpt.get();
                    if (!usuario.getTenants().contains(entity)) {
                        usuario.getTenants().add(entity);
                        usuarioRepository.save(usuario);
                        log.info("Tenant '{}' associado automaticamente ao usuário '{}'", entity.getNome(), email);

                        // Atualizar atributo 'tenants' no Keycloak com lista de slugs
                        atualizarTenantsNoKeycloak(id, usuario);
                    }
                } else {
                    log.warn("Usuário com email '{}' não encontrado para associar ao tenant", email);
                }
            }
        }

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

    @Override
    public List<Tenant> buscarTenantPorUsuario(Long usuarioId) {
        return tenantRepository.findByUsuariosId(usuarioId).stream()
                .map(tenantMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Atualiza o atributo 'tenants' no perfil do usuário no Keycloak com a lista de slugs
     */
    private void atualizarTenantsNoKeycloak(String id, UsuarioEntity usuario) {

            List<String> tenantSlugs = usuario.getTenants().stream()
                    .map(TenantEntity::getSlug)
                    .collect(Collectors.toList());
            
            Map<String, List<String>> atributos = new HashMap<>();
            atributos.put("tenants", tenantSlugs);
            
            boolean sucesso = verifikaAdapter.atualizarAtributos(id, atributos);
            
            if (sucesso) {
                log.info("Atributo 'tenants' atualizado no Keycloak para usuário {}: {}", usuario.getEmail(), tenantSlugs);
            } else {
                log.warn("Não foi possível atualizar atributo 'tenants' no Keycloak para usuário {}", usuario.getEmail());
            }

    }
}
