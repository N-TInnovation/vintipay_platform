package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByUsername(String username);
    
    @Query("SELECT u FROM UsuarioEntity u LEFT JOIN FETCH u.tenants WHERE u.email = :email")
    Optional<UsuarioEntity> findByEmail(@Param("email") String email);
    
    Optional<UsuarioEntity> findByResetPasswordToken(String token);
    Optional<UsuarioEntity> findByEmailVerificationToken(String token);
}
