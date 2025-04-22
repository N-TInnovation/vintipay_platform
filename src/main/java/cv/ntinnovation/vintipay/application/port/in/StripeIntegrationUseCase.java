package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;

import java.util.Map;

public interface StripeIntegrationUseCase {
    String criarCustomer(Usuario usuario);
    String criarProduto(String nome, String descricao);
    String criarPreco(String produtoId, long valorCentavos, String moeda, String intervalo);
    String criarPreco(String produtoId, long valorCentavos, String moeda, String intervalo, int intervalCount);
    void atualizarProduto(String produtoId, String nome, String descricao);
    String criarCheckoutSession(Usuario usuario, String priceId, String successUrl, String cancelUrl);
    String criarSubscription(Usuario usuario, String priceId);
    void cancelarSubscription(String subscriptionId);
    Map<String, Object> processarWebhook(String payload, String sigHeader);
}
