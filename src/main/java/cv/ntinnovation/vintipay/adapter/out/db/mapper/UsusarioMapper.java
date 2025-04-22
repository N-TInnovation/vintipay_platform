package cv.ntinnovation.vintipay.adapter.out.db.mapper;

import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsusarioMapper {

    public Usuario toDomain(UsuarioEntity userEntity) {
        Usuario usuario = Usuario.builder(userEntity.getUsername(),
                        userEntity.getPassword(),
                        userEntity.isAtivo(), userEntity.getRole());
        usuario.setId(userEntity.getId());
        return usuario;
    }

    public UsuarioEntity toEntity(Usuario user) {

        return UsuarioEntity.builder()
                .id(user.getId())
                .username(user.getUsername()).password(user.getPassword())
                .role(user.getRole()[0])
                .ativo(user.isAtivo())
                .build();
    }
}
