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
        usuario.setEmail(userEntity.getEmail());
        usuario.setNome(userEntity.getNome());
        usuario.setTelefone(userEntity.getTelefone());
        usuario.setStripeCustomerId(userEntity.getStripeCustomerId());
        usuario.setDataRegistro(userEntity.getDataRegistro());
        usuario.setUltimoLogin(userEntity.getUltimoLogin());
        return usuario;
    }

    public UsuarioEntity toEntity(Usuario user) {

        return UsuarioEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole()[0])
                .ativo(user.isAtivo())
                .email(user.getEmail())
                .nome(user.getNome())
                .telefone(user.getTelefone())
                .stripeCustomerId(user.getStripeCustomerId())
                .dataRegistro(user.getDataRegistro())
                .ultimoLogin(user.getUltimoLogin())
                .build();
    }
}
