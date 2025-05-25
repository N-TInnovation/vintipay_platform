package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa as regiões no banco de dados
 */
@Entity
@Table(name = "regions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionEntity {

    @Id
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "wiki_data_id")
    private String wikiDataId;
}
