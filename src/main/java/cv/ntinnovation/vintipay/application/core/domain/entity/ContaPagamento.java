package cv.ntinnovation.vintipay.application.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContaPagamento {
	private Long id;
	private InfoContaPagamento acctInfo;
	private String name;
	private String email;
	private String addrMatch;
	private String billAddrCountry;
	private String billAddrCity;
	private String billAddrLine1;
	private String billAddrLine2;
	private String billAddrLine3;
	private String billAddrPostCode;
	private String billAddrState;
	private String shipAddrCountry;
	private String shipAddrCity;
	private String shipAddrLine1;
	private String shipAddrPostCode; 
	private String shipAddrState;
	private ContatoContaPagamento workPhone;
	private ContatoContaPagamento mobilePhone;
}
