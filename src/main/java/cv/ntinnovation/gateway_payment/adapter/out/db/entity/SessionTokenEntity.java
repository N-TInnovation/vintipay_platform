package cv.ntinnovation.gateway_payment.adapter.out.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "sessions")
@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Column(unique = true, nullable = false)
    private String sessionCode;

    private Instant expirationTime;

    public SessionTokenEntity(UserEntity userEntity, String sessionCode, Instant expirationTime) {
        this.userEntity = userEntity;
        this.sessionCode = sessionCode;
        this.expirationTime = expirationTime;
    }

    // Getters e Setters
}

