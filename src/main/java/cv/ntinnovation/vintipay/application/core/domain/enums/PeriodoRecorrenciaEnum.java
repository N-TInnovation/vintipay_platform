package cv.ntinnovation.vintipay.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Enum para representar os diferentes períodos de recorrência disponíveis para produtos
 */
@AllArgsConstructor
@Getter
public enum PeriodoRecorrenciaEnum {
    DIARIO("Diário", 1),
    SEMANAL("Semanal", 7),
    QUINZENAL("Quinzenal", 15),
    MENSAL("Mensal", 30),
    BIMESTRAL("Bimestral", 60),
    TRIMESTRAL("Trimestral", 90),
    SEMESTRAL("Semestral", 180),
    ANUAL("Anual", 365);
    
    private final String descricao;
    private final int dias;

    public LocalDate getDataCobranca() {
        var data = LocalDate.now().plusDays(dias);

        // Se cair em sábado ou domingo, vai para o próximo dia útil
        while (data.getDayOfWeek() == DayOfWeek.SATURDAY ||
                data.getDayOfWeek() == DayOfWeek.SUNDAY) {
            data = data.plusDays(1);
        }

        return data;
    }
}
