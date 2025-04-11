package cv.ntinnovation.gateway_payment.adapter.in.web.mapper;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.ContaPagamentoRequest;
import cv.ntinnovation.gateway_payment.adapter.in.web.dto.ContaPagamentoResponse;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.InfoContaPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class ContaPagamentoWebMapperTest {

    private ContaPagamentoWebMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ContaPagamentoWebMapper();
    }

    @Test
    void toContaPagamento_DeveMapearCorretamente() {
        // Arrange
        ContaPagamentoRequest request = ContaPagamentoRequest.builder()
                .email("cliente@example.com")
                .billAddrCity("Praia")
                .billAddrCountry("CV")
                .billAddrLine1("Rua Principal, 123")
                .billAddrPostCode("12345")
                .build();

        // Act
        ContaPagamento result = mapper.toContaPagamento(request);

        // Assert
        assertNotNull(result);
        assertEquals("cliente@example.com", result.getEmail());
        assertEquals("Praia", result.getBillAddrCity());
        assertEquals("CV", result.getBillAddrCountry());
        assertEquals("Rua Principal, 123", result.getBillAddrLine1());
        assertEquals("12345", result.getBillAddrPostCode());
    }

    @Test
    void toContaPagamentoResponse_DeveMapearCorretamente() {
        // Arrange
        ContaPagamento contaPagamento = createSampleContaPagamento();

        // Act
        ContaPagamentoResponse result = mapper.toContaPagamentoResponse(contaPagamento);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("cliente@example.com", result.getEmail());
        assertEquals("Praia", result.getBillAddrCity());
        assertEquals("CV", result.getBillAddrCountry());
        assertEquals("Rua Principal, 123", result.getBillAddrLine1());
        assertEquals("12345", result.getBillAddrPostCode());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void toContaPagamento_QuandoRequestNulo_DeveRetornarNull() {
        // Act
        ContaPagamento result = mapper.toContaPagamento(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toContaPagamentoResponse_QuandoEntityNula_DeveRetornarNull() {
        // Act
        ContaPagamentoResponse result = mapper.toContaPagamentoResponse(null);

        // Assert
        assertNull(result);
    }

    private ContaPagamento createSampleContaPagamento() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String dataFormatada = agora.format(formatter);
        
        InfoContaPagamento acctInfo = InfoContaPagamento.builder()
                .acctID("ACC123456789")
                .chAccAgeInd("01")
                .chAccDate(dataFormatada)
                .chAccChange(dataFormatada)
                .chAccPwChange(dataFormatada)
                .chAccPwChangeInd("01")
                .suspiciousAccActivity("01")
                .build();
                
        return ContaPagamento.builder()
                .id(1L)
                .email("cliente@example.com")
                .billAddrCity("Praia")
                .billAddrCountry("CV")
                .billAddrLine1("Rua Principal, 123")
                .billAddrPostCode("12345")
                .acctInfo(acctInfo)
                .build();
    }
}
