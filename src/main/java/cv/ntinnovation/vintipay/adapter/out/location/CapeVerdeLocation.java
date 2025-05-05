package cv.ntinnovation.vintipay.adapter.out.location;

import cv.ntinnovation.vintipay.adapter.out.persistence.dto.CapeVerdeLocationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapeVerdeLocation {
    private Long id;
    private String island;
    private String county;
    private String parish;
    private String zone;
    private String subzone;
    private String name;
    
    /**
     * Converte um DTO para o modelo de localização de Cabo Verde
     * @param dto DTO de localização de Cabo Verde
     * @return Modelo de localização de Cabo Verde
     */
    public static CapeVerdeLocation fromDTO(CapeVerdeLocationDTO dto) {
        return new CapeVerdeLocation(
                dto.getId(),
                dto.getIsland(),
                dto.getCounty(),
                dto.getParish(),
                dto.getZone(),
                dto.getSubzone(),
                dto.getName()
        );
    }
}
