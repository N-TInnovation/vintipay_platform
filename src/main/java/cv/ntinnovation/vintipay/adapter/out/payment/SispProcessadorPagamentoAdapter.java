package cv.ntinnovation.vintipay.adapter.out.payment;

import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;
import cv.ntinnovation.vintipay.application.port.out.ProcessadorPagamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adaptador para integração com o serviço de pagamento SISP
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SispProcessadorPagamentoAdapter implements ProcessadorPagamento {

    private final RestTemplate restTemplate;
    
    @Value("${sisp.api.url:https://api.sisp.cv}")
    private String sispApiUrl;
    
    @Value("${sisp.api.key:}")
    private String sispApiKey;
    
    @Value("${sisp.merchant.id:}")
    private String sispMerchantId;
    
    // Cache local para armazenar sessões de pagamento (em produção, isso deveria ser armazenado em um banco de dados)
    private final Map<String, SessaoPagamento> sessoesPagamento = new ConcurrentHashMap<>();

    @Override
    public SessaoPagamento criarSessaoPagamento(String referencia, String nome, String descricao, 
                                              BigDecimal valor, String moeda,
                                              String urlRetornoSucesso, String urlRetornoCancelamento) {
        log.info("Criando sessão de pagamento SISP: {}, {}, {}", referencia, nome, valor);
        
        try {
            // Em um ambiente real, aqui você faria uma chamada para a API da SISP
            // Exemplo:
            /*
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("reference", referencia);
            requestBody.put("amount", valor);
            requestBody.put("currency", moeda);
            requestBody.put("description", descricao);
            requestBody.put("success_url", urlRetornoSucesso);
            requestBody.put("cancel_url", urlRetornoCancelamento);
            requestBody.put("merchant_id", sispMerchantId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + sispApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                sispApiUrl + "/checkout/sessions",
                request,
                Map.class
            );
            
            Map<String, Object> responseBody = response.getBody();
            String sessionId = (String) responseBody.get("session_id");
            String checkoutUrl = (String) responseBody.get("checkout_url");
            */
            
            // Como não temos acesso real à API da SISP, vamos simular a resposta
            String sessionId = "sisp_session_" + referencia;
            String checkoutUrl = "https://checkout.sisp.cv/?session=" + sessionId;
            
            // Criar a sessão de pagamento
            SessaoPagamento sessao = SessaoPagamento.builder()
                    .sessionId(sessionId)
                    .referencia(referencia)
                    .valor(valor)
                    .moeda(moeda)
                    .descricao(descricao)
                    .dataCriacao(LocalDateTime.now())
                    .checkoutUrl(checkoutUrl)
                    .urlRetornoSucesso(urlRetornoSucesso)
                    .urlRetornoCancelamento(urlRetornoCancelamento)
                    .build();
            
            // Armazenar a sessão no cache local
            sessoesPagamento.put(sessionId, sessao);
            
            return sessao;
        } catch (Exception e) {
            log.error("Erro ao criar sessão de pagamento SISP", e);
            throw new RuntimeException("Erro ao criar sessão de pagamento", e);
        }
    }
    
    @Override
    public SessaoPagamento criarSessaoPagamento(String referencia, String nome, String descricao, 
                                              BigDecimal valor, String moeda,
                                              String urlRetornoSucesso, String urlRetornoCancelamento,
                                              ContaPagamento contaPagamento, String merchantSession) {
        log.info("Criando sessão de pagamento SISP com dados do comprador: {}, {}, {}, email: {}", 
                referencia, nome, valor, contaPagamento.getEmail());
        
        try {
            // Em um ambiente real, aqui você faria uma chamada para a API da SISP incluindo os dados do comprador
            // Exemplo:
            /*
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("reference", referencia);
            requestBody.put("amount", valor);
            requestBody.put("currency", moeda);
            requestBody.put("description", descricao);
            requestBody.put("success_url", urlRetornoSucesso);
            requestBody.put("cancel_url", urlRetornoCancelamento);
            requestBody.put("merchant_id", sispMerchantId);
            requestBody.put("merchant_session", merchantSession);
            
            // Adicionar dados do comprador
            Map<String, Object> customerData = new HashMap<>();
            customerData.put("email", contaPagamento.getEmail());
            customerData.put("billAddrCity", contaPagamento.getBillAddrCity());
            customerData.put("billAddrCountry", contaPagamento.getBillAddrCountry());
            customerData.put("billAddrLine1", contaPagamento.getBillAddrLine1());
            customerData.put("billAddrPostCode", contaPagamento.getBillAddrPostCode());
            
            if (contaPagamento.getMobilePhone() != null) {
                Map<String, Object> phoneData = new HashMap<>();
                phoneData.put("cc", contaPagamento.getMobilePhone().getCc());
                phoneData.put("subscriber", contaPagamento.getMobilePhone().getSubscriber());
                customerData.put("mobilePhone", phoneData);
            }
            
            requestBody.put("customer", customerData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + sispApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                sispApiUrl + "/checkout/sessions",
                request,
                Map.class
            );
            
            Map<String, Object> responseBody = response.getBody();
            String sessionId = (String) responseBody.get("session_id");
            String checkoutUrl = (String) responseBody.get("checkout_url");
            */
            
            // Como não temos acesso real à API da SISP, vamos simular a resposta
            String sessionId = "sisp_session_" + referencia;
            String checkoutUrl = "/pagamento/checkout/" + referencia + "?sessionId=" + sessionId;
            
            // Criar a sessão de pagamento
            SessaoPagamento sessao = SessaoPagamento.builder()
                    .sessionId(sessionId)
                    .referencia(referencia)
                    .valor(valor)
                    .moeda(moeda)
                    .descricao(descricao)
                    .dataCriacao(LocalDateTime.now())
                    .checkoutUrl(checkoutUrl)
                    .urlRetornoSucesso(urlRetornoSucesso)
                    .urlRetornoCancelamento(urlRetornoCancelamento)
                    .build();
            
            // Armazenar a sessão no cache local
            sessoesPagamento.put(sessionId, sessao);
            
            return sessao;
        } catch (Exception e) {
            log.error("Erro ao criar sessão de pagamento SISP com dados do comprador", e);
            throw new RuntimeException("Erro ao criar sessão de pagamento", e);
        }
    }

    @Override
    public Optional<SessaoPagamento> buscarSessaoPorId(String sessionId) {
        log.info("Buscando sessão de pagamento SISP: {}", sessionId);
        
        try {
            // Em um ambiente real, aqui você faria uma chamada para a API da SISP
            // Exemplo:
            /*
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + sispApiKey);
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                sispApiUrl + "/checkout/sessions/" + sessionId,
                HttpMethod.GET,
                request,
                Map.class
            );
            
            Map<String, Object> responseBody = response.getBody();
            */
            
            // Como não temos acesso real à API da SISP, vamos usar o cache local
            return Optional.ofNullable(sessoesPagamento.get(sessionId));
        } catch (Exception e) {
            log.error("Erro ao buscar sessão de pagamento SISP", e);
            return Optional.empty();
        }
    }

    @Override
    public boolean verificarStatusPagamento(String sessionId) {
        log.info("Verificando status de pagamento SISP: {}", sessionId);
        
        try {
            // Em um ambiente real, aqui você faria uma chamada para a API da SISP
            // Exemplo:
            /*
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + sispApiKey);
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                sispApiUrl + "/checkout/sessions/" + sessionId,
                HttpMethod.GET,
                request,
                Map.class
            );
            
            Map<String, Object> responseBody = response.getBody();
            String status = (String) responseBody.get("status");
            
            return "paid".equals(status);
            */
            
            // Como não temos acesso real à API da SISP, vamos assumir que o pagamento foi bem-sucedido
            return true;
        } catch (Exception e) {
            log.error("Erro ao verificar status de pagamento SISP", e);
            return false;
        }
    }

    @Override
    public boolean cancelarSessaoPagamento(String sessionId) {
        log.info("Cancelando sessão de pagamento SISP: {}", sessionId);
        
        try {
            // Em um ambiente real, aqui você faria uma chamada para a API da SISP
            // Exemplo:
            /*
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + sispApiKey);
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                sispApiUrl + "/checkout/sessions/" + sessionId + "/cancel",
                HttpMethod.POST,
                request,
                Map.class
            );
            
            return response.getStatusCode().is2xxSuccessful();
            */
            
            // Como não temos acesso real à API da SISP, vamos simular o cancelamento
            Optional<SessaoPagamento> sessaoOpt = buscarSessaoPorId(sessionId);
            if (sessaoOpt.isPresent()) {
                sessoesPagamento.remove(sessionId);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("Erro ao cancelar sessão de pagamento SISP", e);
            return false;
        }
    }
}
