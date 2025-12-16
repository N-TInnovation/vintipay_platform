package cv.ntinnovation.vintipay.adapter.out.persistence.mapper;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PapelEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.TenantEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.Papel;
import cv.ntinnovation.vintipay.application.core.domain.entity.Tenant;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    private final PapelMapper papelMapper;
    private final TenantMapper tenantMapper;
    
    public UsuarioMapper(PapelMapper papelMapper, @Lazy TenantMapper tenantMapper) {
        this.papelMapper = papelMapper;
        this.tenantMapper = tenantMapper;
    }

    public Usuario toDomain(UsuarioEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        
        // Verificar se o username é nulo
        if (userEntity.getUsername() == null) {
            // Criar um objeto Usuario simplificado apenas com o ID
            Usuario usuario = new Usuario();
            usuario.setId(userEntity.getId());
            return usuario;
        }
        
        // Processar roles - separar por vírgula e remover prefixo ROLE_ se existir
        String[] roles = null;
        if (userEntity.getRole() != null && !userEntity.getRole().isEmpty()) {
            roles = userEntity.getRole().split(",");
            for (int i = 0; i < roles.length; i++) {
                if (roles[i].startsWith("ROLE_")) {
                    roles[i] = roles[i].substring(5);
                }
            }
        } else {
            roles = new String[]{"USER"};
        }
        
        Usuario usuario = Usuario.builder(userEntity.getUsername(),
                    userEntity.getPassword(),
                    userEntity.isAtivo(), 
                    roles);
        usuario.setId(userEntity.getId());
        usuario.setEmail(userEntity.getEmail());
        usuario.setNome(userEntity.getNome());
        usuario.setTelefone(userEntity.getTelefone());
        usuario.setStripeCustomerId(userEntity.getStripeCustomerId());
        usuario.setDataRegistro(userEntity.getDataRegistro());
        usuario.setUltimoLogin(userEntity.getUltimoLogin());
        usuario.setResetPasswordToken(userEntity.getResetPasswordToken());
        usuario.setResetPasswordExpires(userEntity.getResetPasswordExpires());
        usuario.setEmailVerificationToken(userEntity.getEmailVerificationToken());
        usuario.setEmailVerified(userEntity.isEmailVerified());
        usuario.setEmailVerificationExpires(userEntity.getEmailVerificationExpires());
        usuario.setNif(userEntity.getNif());
        
        // Mapear papéis
        if (userEntity.getPapeis() != null && !userEntity.getPapeis().isEmpty()) {
            Set<Papel> papeis = userEntity.getPapeis().stream()
                    .map(papelMapper::toDomain)
                    .collect(Collectors.toSet());
            usuario.setPapeis(papeis);
        }
        
        // Mapear tenants - verificar se está inicializado para evitar LazyInitializationException
        if (Hibernate.isInitialized(userEntity.getTenants()) && 
            userEntity.getTenants() != null && 
            !userEntity.getTenants().isEmpty()) {
            Set<Tenant> tenants = userEntity.getTenants().stream()
                    .map(tenantMapper::toDomainWithoutRelationships)
                    .collect(Collectors.toSet());
            usuario.setTenants(tenants);
        }
        
        return usuario;
    }

    public UsuarioEntity toEntity(Usuario user) {
        if (user == null) {
            return null;
        }
        
        // Se for apenas um ID de referência, criar uma entidade com apenas o ID
        if (user.getUsername() == null) {
            UsuarioEntity entity = new UsuarioEntity();
            entity.setId(user.getId());
            return entity;
        }

        UsuarioEntity entity = UsuarioEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole() != null ? String.join(",", user.getRole()) : "USER")
                .ativo(user.isAtivo())
                .email(user.getEmail())
                .nome(user.getNome())
                .telefone(user.getTelefone())
                .stripeCustomerId(user.getStripeCustomerId())
                .dataRegistro(user.getDataRegistro())
                .ultimoLogin(user.getUltimoLogin())
                .resetPasswordToken(user.getResetPasswordToken())
                .resetPasswordExpires(user.getResetPasswordExpires())
                .emailVerificationToken(user.getEmailVerificationToken())
                .emailVerified(user.isEmailVerified())
                .emailVerificationExpires(user.getEmailVerificationExpires())
                .nif(user.getNif())
                .build();
        
        // Mapear papéis
        if (user.getPapeis() != null && !user.getPapeis().isEmpty()) {
            Set<PapelEntity> papeis = user.getPapeis().stream()
                    .map(papelMapper::toEntity)
                    .collect(Collectors.toSet());
            entity.setPapeis(papeis);
        }
        
        // Nota: Não mapeamos tenants aqui para evitar ciclo de dependência
        // O relacionamento será gerenciado manualmente via repository quando necessário
        
        return entity;
    }
}
