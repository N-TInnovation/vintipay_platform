package cv.ntinnovation.gateway_payment.adapter.out.db.repository;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
