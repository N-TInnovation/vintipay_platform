package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.DescontoCicloEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.ProdutoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para acessar e manipular entidades de descontos por ciclo no banco de dados
 */
@Repository
public interface DescontoCicloRepository extends JpaRepository<DescontoCicloEntity, Long> {
    
    /**
     * Busca todos os descontos associados a um produto específico
     * 
     * @param produto O produto para o qual buscar descontos
     * @return Lista de descontos associados ao produto
     */
    List<DescontoCicloEntity> findByProduto(ProdutoEntity produto);
    
    /**
     * Busca todos os descontos associados a um produto específico, ordenados pelo número do ciclo
     * 
     * @param produto O produto para o qual buscar descontos
     * @return Lista de descontos associados ao produto, ordenados por numeroCiclo
     */
    List<DescontoCicloEntity> findByProdutoOrderByNumeroCicloAsc(ProdutoEntity produto);
    
    /**
     * Busca o desconto para um ciclo específico de um produto
     * 
     * @param produto O produto para o qual buscar o desconto
     * @param numeroCiclo O número do ciclo para o qual buscar o desconto
     * @return O desconto para o ciclo específico, se existir
     */
    DescontoCicloEntity findByProdutoAndNumeroCiclo(ProdutoEntity produto, Integer numeroCiclo);
    
    /**
     * Remove todos os descontos associados a um produto específico
     * 
     * @param produto O produto para o qual remover os descontos
     */
    void deleteByProduto(ProdutoEntity produto);
}
