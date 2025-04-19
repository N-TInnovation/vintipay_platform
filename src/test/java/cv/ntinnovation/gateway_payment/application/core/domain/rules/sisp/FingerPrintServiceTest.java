package cv.ntinnovation.gateway_payment.application.core.domain.rules.sisp;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.DataFingerPrint;
import cv.ntinnovation.gateway_payment.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.FingerPrintResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FingerPrintServiceTest {

    @InjectMocks
    FingerPrintService fingerPrint;

    @Test
    void test_generate_finger_print(){
        DataFingerPrint dataFingerPrint = DataFingerPrint.builder()
                .posID("90000187")
                .referenceNumber("000000")
                .merchantRef("R00000001991550")
                .merchantSession("S00000003001491")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .amount(BigDecimal.valueOf(100))
                .currency("132")
                .entityCode("00001")
                .transactionCode("0")
                .posAutCode("DjZDExoROaG0vZoQ")
                .build();
        FingerPrintResult fingerPrintResult = fingerPrint.generateFingerPrint(dataFingerPrint);
        assertNotNull(fingerPrintResult);

    }

}