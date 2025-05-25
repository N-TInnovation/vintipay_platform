package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CountryEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.StateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<StateEntity, Long> {
    
    List<StateEntity> findByCountryOrderByNameAsc(CountryEntity country);
    
    List<StateEntity> findByCountryIso2OrderByNameAsc(String countryIso2);
    
    Optional<StateEntity> findByCountryCodeAndStateCode(String countryCode, String stateCode);
    
    Optional<StateEntity> findByStateCodeAndCountry_Id(String stateCode, Long countryId);
    
    default Optional<StateEntity> findByStateCodeAndCountryId(String stateCode, Long countryId) {
        return findByStateCodeAndCountry_Id(stateCode, countryId);
    }
    
    @Modifying
    @Query("DELETE FROM StateEntity")
    void clearAll();
    
    default void clear() {
        // Este método é usado para limpeza periódica do contexto de persistência
        // durante o processamento em lote
    }
}
