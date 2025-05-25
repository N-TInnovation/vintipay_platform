package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "states")
public class StateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity country;

    @Column(name = "country_code", length = 2)
    private String countryCode;
    
    @Column(name = "country_name")
    private String countryName;

    @Column(name = "fips_code")
    private String fipsCode;

    @Column(name = "iso2")
    private String iso2;

    @Column(name = "state_code")
    private String stateCode;

    private String type;

    @Column(length = 50)
    private String latitude;

    @Column(length = 50)
    private String longitude;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private Boolean flag;

    @Column(name = "wikiDataId")
    private String wikiDataId;

    @OneToMany(mappedBy = "state")
    private List<CityEntity> cities;
}
