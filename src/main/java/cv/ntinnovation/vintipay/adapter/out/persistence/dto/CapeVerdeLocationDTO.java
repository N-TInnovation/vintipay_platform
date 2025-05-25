package cv.ntinnovation.vintipay.adapter.out.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapeVerdeLocationDTO {
    private Long id;
    private String codigo;
    private Integer nivel;
    private String pais;
    private String ilha;
    private String concelho;
    private String freguesia;
    private String zona;
    private String lugar;
    private String nome;
    
    // Mantém os campos antigos para compatibilidade com código legado
    public String getIsland() {
        return ilha;
    }
    
    public void setIsland(String ilha) {
        this.ilha = ilha;
    }
    
    public String getCounty() {
        return concelho;
    }
    
    public void setCounty(String concelho) {
        this.concelho = concelho;
    }
    
    public String getParish() {
        return freguesia;
    }
    
    public void setParish(String freguesia) {
        this.freguesia = freguesia;
    }
    
    public String getZone() {
        return zona;
    }
    
    public void setZone(String zona) {
        this.zona = zona;
    }
    
    public String getSubzone() {
        return lugar;
    }
    
    public void setSubzone(String lugar) {
        this.lugar = lugar;
    }
    
    public String getName() {
        return nome;
    }
    
    public void setName(String nome) {
        this.nome = nome;
    }
}
