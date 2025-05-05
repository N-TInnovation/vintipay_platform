package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CountryEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.StateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<StateEntity, Long> {
    
    List<StateEntity> findByCountryOrderByNameAsc(CountryEntity country);
    
    List<StateEntity> findByCountryIso2OrderByNameAsc(String countryIso2);
    
    Optional<StateEntity> findByCountryCodeAndStateCode(String countryCode, String stateCode);
}
