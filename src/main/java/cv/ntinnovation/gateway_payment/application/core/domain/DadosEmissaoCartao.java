package cv.ntinnovation.gateway_payment.application.core.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class DadosEmissaoCartao {
    private String name;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;

    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    private String cardholderId;

    private BigDecimal limit;
    private String status;

    private String exp_month;
    private String exp_year;
    private String brand;
    private String last4;
}
