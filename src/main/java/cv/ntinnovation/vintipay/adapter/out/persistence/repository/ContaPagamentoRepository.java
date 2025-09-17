package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.ContaPagamentoEntity;

import java.util.Optional;

@Repository
public interface ContaPagamentoRepository extends JpaRepository<ContaPagamentoEntity, Long> {
    Optional<ContaPagamentoEntity> findByEmail(String email);
    
    /**
     * Busca contas de pagamento por nome ou email contendo o texto da query
     * 
     * @param query Texto a ser buscado no nome ou email
     * @param pageable Configuração de paginação
     * @return Página com os resultados da busca
     */
    @Query("SELECT c FROM ContaPagamentoEntity c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<ContaPagamentoEntity> findByNameOrEmailContainingIgnoreCase(@Param("query") String query, Pageable pageable);
}
