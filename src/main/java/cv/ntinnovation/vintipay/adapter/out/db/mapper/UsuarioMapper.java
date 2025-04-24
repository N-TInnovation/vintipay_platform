package cv.ntinnovation.vintipay.adapter.out.db.mapper;

import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.PapelMapper;
import cv.ntinnovation.vintipay.adapter.out.db.entity.PapelEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.Papel;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    private final PapelMapper papelMapper;

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
        
        // Mapear papéis
        if (userEntity.getPapeis() != null && !userEntity.getPapeis().isEmpty()) {
            Set<Papel> papeis = userEntity.getPapeis().stream()
                    .map(papelMapper::toDomain)
                    .collect(Collectors.toSet());
            usuario.setPapeis(papeis);
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
                .build();
        
        // Mapear papéis
        if (user.getPapeis() != null && !user.getPapeis().isEmpty()) {
            Set<PapelEntity> papeis = user.getPapeis().stream()
                    .map(papelMapper::toEntity)
                    .collect(Collectors.toSet());
            entity.setPapeis(papeis);
        }
        
        return entity;
    }
}
