package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CapeVerdeLocationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CapeVerdeLocationsRepository extends JpaRepository<CapeVerdeLocationsEntity, Long> {
    
    /**
     * Encontra todas as ilhas distintas de Cabo Verde
     * @return Lista de ilhas
     */
    @Query("SELECT DISTINCT c.island FROM CapeVerdeLocationsEntity c WHERE c.island IS NOT NULL AND c.zone is null AND c.subzone is null AND c.parish is null AND c.county is null ORDER BY c.island, c.name")
    List<String> findDistinctIsland();
    
    /**
     * Encontra todas as ilhas distintas de Cabo Verde com seus nomes para exibição
     * @return Lista de objetos com valor e label
     */
@Query("SELECT NEW cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO(c.island, c.name) FROM CapeVerdeLocationsEntity c WHERE c.island IS NOT NULL AND c.island <> '' AND c.zone = '' AND c.subzone = '' AND c.parish = '' AND c.county = '' GROUP BY c.island, c.name ORDER BY c.island")
    List<LocationOptionDTO> findDistinctIslandWithNames();
    
    /**
     * Encontra todos os concelhos de uma ilha específica
     * @param island Nome da ilha
     * @return Lista de concelhos
     */
    @Query("SELECT DISTINCT c.county FROM CapeVerdeLocationsEntity c WHERE c.island = ?1 ORDER BY c.county")
    List<String> findDistinctCountyByIsland(String island);
    
    /**
     * Encontra todos os concelhos de uma ilha específica com seus nomes para exibição
     * @param island Nome da ilha
     * @return Lista de objetos com valor e label
     */
    @Query("SELECT NEW cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO(c.county, c.name) FROM CapeVerdeLocationsEntity c WHERE c.island = ?1 AND c.county IS NOT NULL AND c.zone = '' AND c.subzone = '' AND c.parish = '' AND c.county <> '' GROUP BY c.county, c.name ORDER BY c.county")
    List<LocationOptionDTO> findDistinctCountyByIslandWithNames(String island);
    
    /**
     * Encontra todas as freguesias de um concelho específico
     * @param county Nome do concelho
     * @return Lista de freguesias
     */
    @Query("SELECT DISTINCT c.parish FROM CapeVerdeLocationsEntity c WHERE c.county = ?1 ORDER BY c.parish")
    List<String> findDistinctParishByCounty(String county);
    
    /**
     * Encontra todas as freguesias de um concelho específico com seus nomes para exibição
     * @param county Nome do concelho
     * @return Lista de objetos com valor e label
     */
    @Query("SELECT NEW cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO(c.parish, c.name) FROM CapeVerdeLocationsEntity c WHERE c.county = ?1 AND c.parish IS NOT NULL AND c.zone = '' AND c.subzone = '' AND c.parish <> ''  GROUP BY c.parish, c.name ORDER BY c.parish")
    List<LocationOptionDTO> findDistinctParishByCountyWithNames(String county);
    
    /**
     * Encontra todas as zonas de uma freguesia específica
     * @param parish Nome da freguesia
     * @return Lista de zonas
     */
    @Query("SELECT DISTINCT c.zone FROM CapeVerdeLocationsEntity c WHERE c.parish = ?1 ORDER BY c.zone")
    List<String> findDistinctZoneByParish(String parish);
    
    /**
     * Encontra todas as zonas de uma freguesia específica com seus nomes para exibição
     * @param parish Nome da freguesia
     * @return Lista de objetos com valor e label
     */
    @Query("SELECT NEW cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO(c.zone, c.name) FROM CapeVerdeLocationsEntity c WHERE c.parish = ?1 AND c.zone IS NOT NULL AND c.zone <> '' AND c.subzone = '' GROUP BY c.zone, c.name ORDER BY c.zone")
    List<LocationOptionDTO> findDistinctZoneByParishWithNames(String parish);
    
    /**
     * Encontra todas as subzonas de uma zona específica
     * @param zone Nome da zona
     * @return Lista de subzonas
     */
@Query("SELECT DISTINCT c.subzone FROM CapeVerdeLocationsEntity c WHERE c.zone = ?1 AND c.zone <> '' AND c.subzone = '' ORDER BY c.subzone")
    List<String> findDistinctSubzoneByZone(String zone);
    
    /**
     * Encontra todas as subzonas de uma zona específica com seus nomes para exibição
     * @param zone Nome da zona
     * @return Lista de objetos com valor e label
     */
    @Query("SELECT NEW cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO(c.subzone, c.name) FROM CapeVerdeLocationsEntity c WHERE c.zone = ?1 AND c.subzone IS NOT NULL AND c.subzone <> '' GROUP BY c.subzone, c.name ORDER BY c.subzone")
    List<LocationOptionDTO> findDistinctSubzoneByZoneWithNames(String zone);
}
