package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, Long> {
    
    Optional<CountryEntity> findByIso2(String iso2);
    
    Optional<CountryEntity> findByNumericCode(String numericCode);
    
    List<CountryEntity> findAllByOrderByNameAsc();
}
