package cv.ntinnovation.vintipay.adapter.out.persistence.adapter;

import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.VerifikaAdapter;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.TenantEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.TenantMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.TenantRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantAdapterTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private TenantMapper tenantMapper;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private VerifikaAdapter keycloakAdminAdapter;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomOAuth2User customOAuth2User;

    @InjectMocks
    private TenantAdapter tenantAdapter;

    private Tenant tenant;
    private TenantEntity tenantEntity;
    private UsuarioEntity usuarioEntity;

    @BeforeEach
    void setUp() {
        tenant = Tenant.builder()
                .id(1L)
                .codigo("TEST001")
                .slug("tenant-teste")
                .nome("Tenant Teste")
                .email("tenant@teste.com")
                .ativo(true)
                .createdAt(LocalDateTime.now())
                .build();

        tenantEntity = new TenantEntity();
        tenantEntity.setId(1L);
        tenantEntity.setCodigo("TEST001");
        tenantEntity.setSlug("tenant-teste");
        tenantEntity.setNome("Tenant Teste");
        tenantEntity.setEmail("tenant@teste.com");
        tenantEntity.setAtivo(true);
        tenantEntity.setCreatedAt(LocalDateTime.now());

        usuarioEntity = new UsuarioEntity();
        usuarioEntity.setId(1L);
        usuarioEntity.setEmail("usuario@teste.com");
        usuarioEntity.setUsername("usuario.teste");
        usuarioEntity.setTenants(new HashSet<>());
    }

    @Test
    void deveSalvarTenantEAssociarAoUsuarioLogado() {
        when(tenantMapper.toEntity(tenant)).thenReturn(tenantEntity);
        when(tenantRepository.save(tenantEntity)).thenReturn(tenantEntity);
        when(tenantMapper.toDomain(tenantEntity)).thenReturn(tenant);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customOAuth2User);
        when(customOAuth2User.getId()).thenReturn("user-id-123");
        when(customOAuth2User.getEmail()).thenReturn("usuario@teste.com");
        
        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(usuarioEntity));
        when(usuarioRepository.save(usuarioEntity)).thenReturn(usuarioEntity);
        when(keycloakAdminAdapter.atualizarAtributos(anyString(), anyMap())).thenReturn(true);

        Tenant resultado = tenantAdapter.salvar(tenant);

        assertNotNull(resultado);
        assertEquals(tenant.getId(), resultado.getId());
        
        verify(tenantRepository).save(tenantEntity);
        verify(usuarioRepository).findByEmail("usuario@teste.com");
        verify(usuarioRepository).save(usuarioEntity);
        verify(keycloakAdminAdapter).atualizarAtributos(eq("user-id-123"), anyMap());
        assertTrue(usuarioEntity.getTenants().contains(tenantEntity));
    }

    @Test
    void deveSalvarTenantSemAssociarQuandoUsuarioNaoEstiverLogado() {
        when(tenantMapper.toEntity(tenant)).thenReturn(tenantEntity);
        when(tenantRepository.save(tenantEntity)).thenReturn(tenantEntity);
        when(tenantMapper.toDomain(tenantEntity)).thenReturn(tenant);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        Tenant resultado = tenantAdapter.salvar(tenant);

        assertNotNull(resultado);
        assertEquals(tenant.getId(), resultado.getId());
        
        verify(tenantRepository).save(tenantEntity);
        verify(usuarioRepository, never()).findByEmail(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveSalvarTenantSemAssociarQuandoUsuarioNaoEncontrado() {
        when(tenantMapper.toEntity(tenant)).thenReturn(tenantEntity);
        when(tenantRepository.save(tenantEntity)).thenReturn(tenantEntity);
        when(tenantMapper.toDomain(tenantEntity)).thenReturn(tenant);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customOAuth2User);
        when(customOAuth2User.getId()).thenReturn("user-id-123");
        when(customOAuth2User.getEmail()).thenReturn("usuario@teste.com");
        
        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.empty());

        Tenant resultado = tenantAdapter.salvar(tenant);

        assertNotNull(resultado);
        assertEquals(tenant.getId(), resultado.getId());
        
        verify(tenantRepository).save(tenantEntity);
        verify(usuarioRepository).findByEmail("usuario@teste.com");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoDeveAssociarTenantDuplicadoAoUsuario() {
        usuarioEntity.getTenants().add(tenantEntity);

        when(tenantMapper.toEntity(tenant)).thenReturn(tenantEntity);
        when(tenantRepository.save(tenantEntity)).thenReturn(tenantEntity);
        when(tenantMapper.toDomain(tenantEntity)).thenReturn(tenant);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customOAuth2User);
        when(customOAuth2User.getId()).thenReturn("user-id-123");
        when(customOAuth2User.getEmail()).thenReturn("usuario@teste.com");
        
        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(usuarioEntity));

        Tenant resultado = tenantAdapter.salvar(tenant);

        assertNotNull(resultado);
        assertEquals(tenant.getId(), resultado.getId());
        
        verify(tenantRepository).save(tenantEntity);
        verify(usuarioRepository).findByEmail("usuario@teste.com");
        verify(usuarioRepository, never()).save(any());
        verify(keycloakAdminAdapter, never()).atualizarAtributos(anyString(), anyMap());
        assertEquals(1, usuarioEntity.getTenants().size());
    }

    @Test
    void deveLancarExcecaoQuandoOcorrerErroNaAssociacao() {
        when(tenantMapper.toEntity(tenant)).thenReturn(tenantEntity);
        when(tenantRepository.save(tenantEntity)).thenReturn(tenantEntity);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customOAuth2User);
        when(customOAuth2User.getId()).thenReturn("user-id-123");
        when(customOAuth2User.getEmail()).thenReturn("usuario@teste.com");
        
        when(usuarioRepository.findByEmail("usuario@teste.com"))
                .thenThrow(new RuntimeException("Erro ao buscar usuário"));

        assertThrows(RuntimeException.class, () -> tenantAdapter.salvar(tenant));
        
        verify(tenantRepository).save(tenantEntity);
        verify(usuarioRepository).findByEmail("usuario@teste.com");
    }
}
