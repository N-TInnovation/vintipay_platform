package cv.ntinnovation.gateway_payment.adapter.out.db.mapper;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsusarioMapper {

    public Usuario toDomain(UsuarioEntity userEntity) {
        return Usuario.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .role(userEntity.getRole())
                .ativo(userEntity.isAtivo())
                .build();
    }

    public UsuarioEntity toEntity(Usuario user) {
        return UsuarioEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .ativo(user.isAtivo())
                .build();
    }
}
