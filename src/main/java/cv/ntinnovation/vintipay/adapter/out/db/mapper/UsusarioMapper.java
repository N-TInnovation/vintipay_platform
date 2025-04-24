package cv.ntinnovation.vintipay.adapter.out.db.mapper;

import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import org.springframework.stereotype.Component;

/**
 * @deprecated Use {@link UsuarioMapper} instead. This class is kept for backward compatibility.
 */
@Component
public class UsusarioMapper {

    private final UsuarioMapper usuarioMapper;

    public UsusarioMapper(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    public Usuario toDomain(UsuarioEntity userEntity) {
        return usuarioMapper.toDomain(userEntity);
    }

    public UsuarioEntity toEntity(Usuario user) {
        return usuarioMapper.toEntity(user);
    }
}
