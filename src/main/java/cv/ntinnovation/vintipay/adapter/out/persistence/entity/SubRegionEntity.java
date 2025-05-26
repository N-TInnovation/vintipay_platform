package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa as sub-regiões no banco de dados
 */
@Entity
@Table(name = "subregions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubRegionEntity {

    @Id
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private RegionEntity region;
    
    @Column(name = "wiki_data_id")
    private String wikiDataId;
}
