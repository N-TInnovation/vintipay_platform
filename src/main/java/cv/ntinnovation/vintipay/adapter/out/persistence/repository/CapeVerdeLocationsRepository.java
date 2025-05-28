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
    @Query("SELECT DISTINCT c.ilha FROM CapeVerdeLocationsEntity c WHERE c.ilha IS NOT NULL AND c.zona is null AND c.lugar is null AND c.freguesia is null AND c.concelho is null ORDER BY c.ilha, c.nome")
    List<String> findDistinctIlha();
    
    /**
     * Encontra todas as ilhas distintas de Cabo Verde com seus nomes para exibição
     * @return Lista de objetos com valor e label
     */
    @Query("SELECT NEW cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO(c.ilha, c.nome) FROM CapeVerdeLocationsEntity c WHERE c.nivel = 2 GROUP BY c.ilha, c.nome ORDER BY c.ilha")
    List<LocationOptionDTO> findDistinctIlhaWithNames();
    
    /**
     * Encontra todos os concelhos de uma ilha específica
     * @param ilha Nome da ilha
     * @return Lista de concelhos
     */
    @Query("SELECT DISTINCT c.concelho FROM CapeVerdeLocationsEntity c WHERE c.ilha = ?1 ORDER BY c.concelho")
    List<String> findDistinctConcelhoByIlha(String ilha);
    
    /**
     * Encontra todos os concelhos de uma ilha específica com seus nomes para exibição
     * @param ilha Nome da ilha
     * @return Lista de objetos com valor e label
     */
    @Query("SELECT NEW cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO(c.concelho, c.nome) FROM CapeVerdeLocationsEntity c WHERE c.ilha = ?1 AND c.nivel = 3 GROUP BY c.concelho, c.nome ORDER BY c.concelho")
    List<LocationOptionDTO> findDistinctConcelhoByIlhaWithNames(String ilha);
    
    /**
     * Encontra todas as freguesias de um concelho específico
     * @param concelho Nome do concelho
     * @return Lista de freguesias
     */
    @Query("SELECT DISTINCT c.freguesia FROM CapeVerdeLocationsEntity c WHERE c.concelho = ?1 ORDER BY c.freguesia")
    List<String> findDistinctFreguesiaByConcelho(String concelho);
    
    /**
     * Encontra todas as freguesias de um concelho específico com seus nomes para exibição
     * @param concelho Nome do concelho
     * @return Lista de objetos com valor e label
     */
    @Query("SELECT NEW cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO(c.freguesia, c.nome) FROM CapeVerdeLocationsEntity c WHERE c.concelho = ?1 AND c.nivel = 4 GROUP BY c.freguesia, c.nome ORDER BY c.freguesia")
    List<LocationOptionDTO> findDistinctFreguesiaByConcelhoWithNames(String concelho);
    
    /**
     * Encontra todas as zonas de uma freguesia específica
     * @param freguesia Nome da freguesia
     * @return Lista de zonas
     */
    @Query("SELECT DISTINCT c.zona FROM CapeVerdeLocationsEntity c WHERE c.freguesia = ?1 ORDER BY c.zona")
    List<String> findDistinctZonaByFreguesia(String freguesia);
    
    /**
     * Encontra todas as zonas de uma freguesia específica com seus nomes para exibição
     * @param freguesia Nome da freguesia
     * @return Lista de objetos com valor e label
     */
    @Query("SELECT NEW cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO(c.zona, c.nome) FROM CapeVerdeLocationsEntity c WHERE c.freguesia = ?1 AND c.nivel = 5 GROUP BY c.zona, c.nome ORDER BY c.zona")
    List<LocationOptionDTO> findDistinctZonaByFreguesiaWithNames(String freguesia);
    
    /**
     * Encontra todos os lugares de uma zona específica
     * @param zona Nome da zona
     * @return Lista de lugares
     */
    @Query("SELECT DISTINCT c.lugar FROM CapeVerdeLocationsEntity c WHERE c.zona = ?1 AND c.zona <> '' AND c.lugar <> '' ORDER BY c.lugar")
    List<String> findDistinctLugarByZona(String zona);
    
    /**
     * Encontra todos os lugares de uma zona específica com seus nomes para exibição
     * @param zona Nome da zona
     * @return Lista de objetos com valor e label
     */
    @Query("SELECT NEW cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO(c.lugar, c.nome) FROM CapeVerdeLocationsEntity c WHERE c.zona = ?1 AND c.nivel = 6 GROUP BY c.lugar, c.nome ORDER BY c.lugar")
    List<LocationOptionDTO> findDistinctLugarByZonaWithNames(String zona);
    
    // Métodos antigos para compatibilidade com código legado
    
    @Query("SELECT DISTINCT c.ilha FROM CapeVerdeLocationsEntity c WHERE c.ilha IS NOT NULL AND c.zona is null AND c.lugar is null AND c.freguesia is null AND c.concelho is null ORDER BY c.ilha, c.nome")
    List<String> findDistinctIsland();
    
    @Query("SELECT DISTINCT c.concelho FROM CapeVerdeLocationsEntity c WHERE c.ilha = ?1 ORDER BY c.concelho")
    List<String> findDistinctCountyByIsland(String island);
    
    @Query("SELECT DISTINCT c.freguesia FROM CapeVerdeLocationsEntity c WHERE c.concelho = ?1 ORDER BY c.freguesia")
    List<String> findDistinctParishByCounty(String county);
    
    @Query("SELECT DISTINCT c.zona FROM CapeVerdeLocationsEntity c WHERE c.freguesia = ?1 ORDER BY c.zona")
    List<String> findDistinctZoneByParish(String parish);
    
    @Query("SELECT DISTINCT c.lugar FROM CapeVerdeLocationsEntity c WHERE c.zona = ?1 ORDER BY c.lugar")
    List<String> findDistinctSubzoneByZone(String zone);
    
    @Query("SELECT NEW cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO(c.ilha, c.nome) FROM CapeVerdeLocationsEntity c WHERE c.ilha IS NOT NULL AND c.ilha <> '' AND c.zona IS NULL AND c.lugar IS NULL AND c.freguesia IS NULL AND c.concelho IS NULL GROUP BY c.ilha, c.nome ORDER BY c.ilha")
    List<LocationOptionDTO> findDistinctIslandWithNames();
    
    @Query("SELECT NEW cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO(c.concelho, c.nome) FROM CapeVerdeLocationsEntity c WHERE c.ilha = ?1 AND c.concelho IS NOT NULL AND c.zona IS NULL AND c.lugar IS NULL AND c.freguesia IS NULL AND c.concelho <> '' GROUP BY c.concelho, c.nome ORDER BY c.concelho")
    List<LocationOptionDTO> findDistinctCountyByIslandWithNames(String island);
    
    @Query("SELECT NEW cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO(c.freguesia, c.nome) FROM CapeVerdeLocationsEntity c WHERE c.concelho = ?1 AND c.freguesia IS NOT NULL AND c.zona IS NULL AND c.lugar IS NULL AND c.freguesia <> ''  GROUP BY c.freguesia, c.nome ORDER BY c.freguesia")
    List<LocationOptionDTO> findDistinctParishByCountyWithNames(String county);
    
    @Query("SELECT NEW cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO(c.zona, c.nome) FROM CapeVerdeLocationsEntity c WHERE c.freguesia = ?1 AND c.zona IS NOT NULL AND c.zona <> '' AND c.lugar IS NULL GROUP BY c.zona, c.nome ORDER BY c.zona")
    List<LocationOptionDTO> findDistinctZoneByParishWithNames(String parish);
    
    @Query("SELECT NEW cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO(c.lugar, c.nome) FROM CapeVerdeLocationsEntity c WHERE c.zona = ?1 AND c.lugar IS NOT NULL AND c.lugar <> '' GROUP BY c.lugar, c.nome ORDER BY c.lugar")
    List<LocationOptionDTO> findDistinctSubzoneByZoneWithNames(String zone);
}
