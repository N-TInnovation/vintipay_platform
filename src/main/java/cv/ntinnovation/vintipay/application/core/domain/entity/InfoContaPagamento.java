package cv.ntinnovation.vintipay.application.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfoContaPagamento {
	private Long id;
	private String chAccAgeInd;
	private String chAccChange;
	private String chAccDate;
	private String chAccPwChange;
	private String chAccPwChangeInd;
	private String suspiciousAccActivity;
}
