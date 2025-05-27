package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cv_locations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapeVerdeLocationsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true)
    private String codigo;

    @Column(name = "nivel")
    private Integer nivel;

    @Column(name = "pais")
    private String pais;

    @Column(name = "ilha")
    private String ilha;

    @Column(name = "concelho")
    private String concelho;

    @Column(name = "freguesia")
    private String freguesia;

    @Column(name = "zona")
    private String zona;

    @Column(name = "lugar")
    private String lugar;

    @Column(name = "nome")
    private String nome;

    @Column(name = "lat")
    private String lat;

    @Column(name = "lng")
    private String lng;
}
