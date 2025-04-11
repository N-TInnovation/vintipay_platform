package cv.ntinnovation.gateway_payment.adapter.in.web.mapper;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.IntencaoPagamentoRequest;
import cv.ntinnovation.gateway_payment.adapter.in.web.dto.IntencaoPagamentoResponse;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.MetodoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.enums.StatusPagamentoEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class IntencaoPagamentoWebMapperTest {

    private IntencaoPagamentoWebMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IntencaoPagamentoWebMapper();
    }

    @Test
    void toIntencaoPagamento_ComContaPagamentoId_DeveMapearCorretamente() {
        // Arrange
        IntencaoPagamentoRequest request = IntencaoPagamentoRequest.builder()
                .amount(BigDecimal.valueOf(100.50))
                .currency("CVE")
                .metodoPagamentoId(1L)
                .contaPagamentoId(2L)
                .merchantRef("MERCHANT123")
                .merchantSession("SESSION456")
                .entityCode("1234")
                .transactionCode("TRANS001")
                .referenceNumber("REF123456")
                .build();

        // Act
        IntencaoPagamento result = mapper.toIntencaoPagamento(request);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100.50), result.getAmount());
        assertEquals("CVE", result.getCurrency());
        assertNotNull(result.getMetodoPagamento());
        assertEquals(1L, result.getMetodoPagamento().getId());
        assertNotNull(result.getContaPagamento());
        assertEquals(2L, result.getContaPagamento().getId());
        assertEquals("MERCHANT123", result.getMerchantRef());
        assertEquals("SESSION456", result.getMerchantSession());
        assertEquals("POS789", result.getPosId());
        assertEquals("1234", result.getEntityCode());
        assertEquals("TRANS001", result.getTransactionCode());
        assertEquals("REF123456", result.getReferenceNumber());
    }
    
    @Test
    void toIntencaoPagamento_ComEmailEEndereco_DeveMapearCorretamente() {
        // Arrange
        IntencaoPagamentoRequest request = IntencaoPagamentoRequest.builder()
                .amount(BigDecimal.valueOf(100.50))
                .currency("CVE")
                .metodoPagamentoId(1L)
                .email("cliente@example.com")
                .billAddrCity("Praia")
                .billAddrCountry("CV")
                .billAddrLine1("Rua Principal, 123")
                .billAddrPostCode("12345")
                .merchantRef("MERCHANT123")
                .merchantSession("SESSION456")
                .entityCode("1234")
                .transactionCode("TRANS001")
                .referenceNumber("REF123456")
                .build();

        // Act
        IntencaoPagamento result = mapper.toIntencaoPagamento(request);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100.50), result.getAmount());
        assertEquals("CVE", result.getCurrency());
        assertNotNull(result.getMetodoPagamento());
        assertEquals(1L, result.getMetodoPagamento().getId());
        assertEquals("cliente@example.com", result.getContaPagamentoEmail());
        assertEquals("Praia", result.getContaPagamentoBillAddrCity());
        assertEquals("CV", result.getContaPagamentoBillAddrCountry());
        assertEquals("Rua Principal, 123", result.getContaPagamentoBillAddrLine1());
        assertEquals("12345", result.getContaPagamentoBillAddrPostCode());
        assertEquals("MERCHANT123", result.getMerchantRef());
        assertEquals("SESSION456", result.getMerchantSession());
        assertEquals("POS789", result.getPosId());
        assertEquals("1234", result.getEntityCode());
        assertEquals("TRANS001", result.getTransactionCode());
        assertEquals("REF123456", result.getReferenceNumber());
    }

    @Test
    void toIntencaoPagamentoResponse_DeveMapearCorretamente() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        IntencaoPagamento entity = IntencaoPagamento.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100.50))
                .status(StatusPagamentoEnum.PENDENTE)
                .reference("REF123")
                .createdAt(now)
                .currency("CVE")
                .metodoPagamento(MetodoPagamento.builder().id(1L).build())
                .contaPagamento(ContaPagamento.builder().id(2L).build())
                .contaPagamentoEmail("cliente@example.com")
                .contaPagamentoBillAddrCity("Praia")
                .contaPagamentoBillAddrCountry("CV")
                .contaPagamentoBillAddrLine1("Rua Principal, 123")
                .contaPagamentoBillAddrPostCode("12345")
                .merchantRef("MERCHANT123")
                .merchantSession("SESSION456")
                .posId("POS789")
                .entityCode("1234")
                .transactionCode("TRANS001")
                .referenceNumber("REF123456")
                .build();

        // Act
        IntencaoPagamentoResponse result = mapper.toIntencaoPagamentoResponse(entity);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BigDecimal.valueOf(100.50), result.getAmount());
        assertEquals(StatusPagamentoEnum.PENDENTE, result.getStatus());
        assertEquals("REF123", result.getReference());
        assertEquals(now, result.getCreatedAt());
        assertEquals("CVE", result.getCurrency());
        assertEquals("MERCHANT123", result.getMerchantRef());
        assertEquals("SESSION456", result.getMerchantSession());
        assertEquals("POS789", result.getPosId());
        assertEquals("1234", result.getEntityCode());
        assertEquals("TRANS001", result.getTransactionCode());
        assertEquals("REF123456", result.getReferenceNumber());
        // Não verificamos os campos de email e endereço na resposta, pois eles não são incluídos no DTO de resposta
    }

    @Test
    void toIntencaoPagamento_QuandoRequestNula_DeveRetornarNull() {
        // Act
        IntencaoPagamento result = mapper.toIntencaoPagamento(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toIntencaoPagamentoResponse_QuandoEntityNula_DeveRetornarNull() {
        // Act
        IntencaoPagamentoResponse result = mapper.toIntencaoPagamentoResponse(null);

        // Assert
        assertNull(result);
    }
}
