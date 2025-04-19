package cv.ntinnovation.gateway_payment.application.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class FingerPrintResult {
	
	private String HashFingerPrint;
	private String HashposAutCode;
	private Boolean Result;
	
}
