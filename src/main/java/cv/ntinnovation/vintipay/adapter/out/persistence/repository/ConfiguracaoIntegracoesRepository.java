package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.ConfiguracaoIntegracoesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracaoIntegracoesRepository extends JpaRepository<ConfiguracaoIntegracoesEntity, Long> {

       // Buscar aplicações associadas a configurações de pagamento do usuário
       @Query("SELECT a FROM ConfiguracaoIntegracoesEntity a " +
              "WHERE a.usuario.id = :usuarioId AND a.isActive = true")
       List<ConfiguracaoIntegracoesEntity> findByUsuarioIdAndActive(Long usuarioId);

       @Query("SELECT a FROM ConfiguracaoIntegracoesEntity a " +
              "WHERE a.id = :id AND a.usuario.id = :usuarioId")
       Optional<ConfiguracaoIntegracoesEntity> findByIdAndUsuarioId(Long id, Long usuarioId);

       @Modifying
       @Transactional
       @Query("DELETE FROM ConfiguracaoIntegracoesEntity a WHERE a.id = :id AND a.usuario.id = :usuarioId")
       void deleteByIdAndUsuarioId(Long id, Long usuarioId);

       List<ConfiguracaoIntegracoesEntity> findByUsuarioId(Long usuarioId);

       List<ConfiguracaoIntegracoesEntity> findByConfiguracaoPagamentoId(Long id);


       @Query("SELECT a FROM ConfiguracaoIntegracoesEntity a " +
               "WHERE a.configuracaoPagamento.id = :id AND a.usuario.id = :usuarioId AND a.name = :tipoIntegracao")
       Optional<ConfiguracaoIntegracoesEntity> existsByConfiguracaoPagamentoIdAndUsuarioIdAndName(Long id, Long usuarioId, String tipoIntegracao);

}
