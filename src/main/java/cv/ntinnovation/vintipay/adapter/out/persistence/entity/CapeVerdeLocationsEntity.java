package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cv_locations")
public class CapeVerdeLocationsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "island")
    private String island;

    @Column(name = "county")
    private String county;

    @Column(name = "parish")
    private String parish;

    @Column(name = "zone")
    private String zone;

    @Column(name = "subzone")
    private String subzone;

    @Column(name = "lat")
    private String lat;

    @Column(name = "lng")
    private String lng;

    @Column(name = "name")
    private String name;
}
