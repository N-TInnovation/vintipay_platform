package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CityEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.StateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {
    
    List<CityEntity> findByStateOrderByNameAsc(StateEntity state);
    
    List<CityEntity> findByStateIdOrderByNameAsc(Long stateId);
    
    List<CityEntity> findByCountryIso2AndStateStateCodeOrderByNameAsc(String countryIso2, String stateCode);
    
    Optional<CityEntity> findByNameAndState_Id(String name, Long stateId);
    
    default Optional<CityEntity> findByNameAndStateId(String name, Long stateId) {
        return findByNameAndState_Id(name, stateId);
    }

    Optional<CityEntity> findByNameAndStateCodeCountryCode(String name, String stateCode, String countryCode);
    
    @Modifying
    @Query("DELETE FROM CityEntity")
    void clearAll();
}
