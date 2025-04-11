package cv.ntinnovation.gateway_payment.application.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class DataFingerPrintOutroResponse {

	private String posAutCode;
	private String messageType;
	private String merchantRespMerchantRef;
	private String merchantRespMerchantSession;
	private String resultFingerPrint;

}
