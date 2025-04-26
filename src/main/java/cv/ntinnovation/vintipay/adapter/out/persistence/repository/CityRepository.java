package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CityEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.StateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {
    
    List<CityEntity> findByStateOrderByNameAsc(StateEntity state);
    
    List<CityEntity> findByStateIdOrderByNameAsc(Long stateId);
    
    List<CityEntity> findByCountryIso2AndStateStateCodeOrderByNameAsc(String countryIso2, String stateCode);
}
