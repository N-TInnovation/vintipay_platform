package cv.ntinnovation.vintipay.adapter.out.client.api.vendus;

import cv.ntinnovation.vintipay.adapter.out.client.api.vendus.dto.VendusReceiptRequest;
import cv.ntinnovation.vintipay.adapter.out.client.api.vendus.dto.VendusReceiptResponse;
import cv.ntinnovation.vintipay.adapter.out.client.api.vendus.mapper.VendusReceiptMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.VendusReceipt;
import cv.ntinnovation.vintipay.application.port.out.VendusServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collections;

/**
 * Serviço para integração com a API Vendus
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VendusService implements VendusServicePort {

    private final VendusApiClient vendusApiClient;
    private final VendusReceiptMapper vendusReceiptMapper;

    public VendusReceipt criarRecibo(String apiKey, Pagamento pagamento) {
        log.info("Criando recibo na Vendus para o pagamento: {}", pagamento.getId());
        
        try {
            VendusReceiptRequest request = criarRequestRecibo(pagamento);
            
            ResponseEntity<VendusReceiptResponse> response = 
                vendusApiClient.criarRecibo(getToken(apiKey), request);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Recibo criado com sucesso na Vendus. ID: {}", response.getBody().getId());
                return vendusReceiptMapper.toVendusReceipt(response.getBody());
            } else {
                log.error("Erro ao criar recibo na Vendus. Status: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("Erro ao criar recibo na Vendus", e);
            return null;
        }
    }
    
    /**
     * Cria o objeto de requisição para a API Vendus
     */
    private VendusReceiptRequest criarRequestRecibo(Pagamento pagamento) {
        // Criar informações do cliente
        VendusReceiptRequest.VendusClientInfo clientInfo = VendusReceiptRequest.VendusClientInfo.builder()
                .name(pagamento.getIntencaoPagamento().getContaPagamento().getName())
                .email(pagamento.getIntencaoPagamento().getContaPagamento().getEmail())
                .address(pagamento.getIntencaoPagamento().getContaPagamentoBillAddrLine1())
                .city(pagamento.getIntencaoPagamento().getContaPagamentoBillAddrCity())
                .postalCode(pagamento.getIntencaoPagamento().getContaPagamentoBillAddrPostCode())
                .country(pagamento.getIntencaoPagamento().getContaPagamentoBillAddrCountry())
                .build();
        
        // Criar item do recibo
        VendusReceiptRequest.VendusReceiptItem item = VendusReceiptRequest.VendusReceiptItem.builder()
                .reference(pagamento.getIntencaoPagamento().getReferencia())
                .name("Pagamento VintiPay #" + pagamento.getIntencaoPagamento().getReferencia())
                .quantity(1)
                .price(pagamento.getIntencaoPagamento().getValor())
                .tax(BigDecimal.ZERO) // Ajustar conforme necessário
                .build();
        
        // Criar requisição completa
        return VendusReceiptRequest.builder()
                .client(clientInfo)
                .items(Collections.singletonList(item))
                .paymentMethod("Online")
                .notes("Pagamento processado via VintiPay")
                .externalReference(pagamento.getIntencaoPagamento().getReferencia())
                .build();
    }
    

    public VendusReceipt consultarRecibo(String apiKey,Long receiptId) {
        log.info("Consultando recibo na Vendus. ID: {}", receiptId);
        
        try {
            ResponseEntity<VendusReceiptResponse> response = 
                vendusApiClient.consultarRecibo(getToken(apiKey), receiptId);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Recibo consultado com sucesso na Vendus. ID: {}", receiptId);
                return vendusReceiptMapper.toVendusReceipt(response.getBody());
            } else {
                log.error("Erro ao consultar recibo na Vendus. Status: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("Erro ao consultar recibo na Vendus", e);
            return null;
        }
    }

    private String getToken(String apiKey) {
        String auth = apiKey + ":";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        return "Basic " + new String(encodedAuth);
    }
}
