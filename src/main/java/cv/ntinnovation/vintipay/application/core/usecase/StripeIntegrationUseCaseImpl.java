package cv.ntinnovation.vintipay.application.core.usecase;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.StripeIntegrationUseCase;
import cv.ntinnovation.vintipay.config.StripeConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeIntegrationUseCaseImpl implements StripeIntegrationUseCase {

    private final StripeConfig stripeConfig;

    @Override
    public String criarCustomer(Usuario usuario) {
        try {
            Map<String, String> metadata = new HashMap<>();
            if (usuario.getId() != null) {
                metadata.put("userId", usuario.getId().toString());
            }
            
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setName(usuario.getNome())
                    .setEmail(usuario.getEmail())
                    .setPhone(usuario.getTelefone())
                    .setMetadata(metadata)
                    .build();

            Customer customer = Customer.create(params);
            return customer.getId();
        } catch (StripeException e) {
            log.error("Erro ao criar customer no Stripe", e);
            throw new RuntimeException("Erro ao criar customer no Stripe: " + e.getMessage(), e);
        }
    }

    @Override
    public String criarProduto(String nome, String descricao) {
        try {
            ProductCreateParams params = ProductCreateParams.builder()
                    .setName(nome)
                    .setDescription(descricao)
                    .build();

            Product product = Product.create(params);
            return product.getId();
        } catch (StripeException e) {
            log.error("Erro ao criar produto no Stripe", e);
            throw new RuntimeException("Erro ao criar produto no Stripe: " + e.getMessage(), e);
        }
    }

    @Override
    public String criarPreco(String produtoId, long valorCentavos, String moeda, String intervalo) {
        try {
            PriceCreateParams.Recurring.Interval intervalEnum;
            try {
                intervalEnum = PriceCreateParams.Recurring.Interval.valueOf(intervalo.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Valor padrão se o intervalo não for válido
                log.warn("Intervalo inválido: {}. Usando MONTH como padrão.", intervalo);
                intervalEnum = PriceCreateParams.Recurring.Interval.MONTH;
            }
            
            PriceCreateParams.Recurring recurring = PriceCreateParams.Recurring.builder()
                    .setInterval(intervalEnum)
                    .build();

            PriceCreateParams params = PriceCreateParams.builder()
                    .setProduct(produtoId)
                    .setUnitAmount(valorCentavos)
                    .setCurrency(moeda)
                    .setRecurring(recurring)
                    .build();

            Price price = Price.create(params);
            return price.getId();
        } catch (StripeException e) {
            log.error("Erro ao criar preço no Stripe", e);
            throw new RuntimeException("Erro ao criar preço no Stripe: " + e.getMessage(), e);
        }
    }

    @Override
    public String criarPreco(String produtoId, long valorCentavos, String moeda, String intervalo, int intervalCount) {
        try {
            PriceCreateParams.Recurring.Interval intervalEnum;
            try {
                intervalEnum = PriceCreateParams.Recurring.Interval.valueOf(intervalo.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Valor padrão se o intervalo não for válido
                log.warn("Intervalo inválido: {}. Usando MONTH como padrão.", intervalo);
                intervalEnum = PriceCreateParams.Recurring.Interval.MONTH;
            }
            
            PriceCreateParams.Recurring recurring = PriceCreateParams.Recurring.builder()
                    .setInterval(intervalEnum)
                    .setIntervalCount((long) intervalCount)
                    .build();

            PriceCreateParams params = PriceCreateParams.builder()
                    .setProduct(produtoId)
                    .setUnitAmount(valorCentavos)
                    .setCurrency(moeda)
                    .setRecurring(recurring)
                    .build();

            Price price = Price.create(params);
            return price.getId();
        } catch (StripeException e) {
            log.error("Erro ao criar preço no Stripe com intervalCount: {}", intervalCount, e);
            throw new RuntimeException("Erro ao criar preço no Stripe: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String criarCheckoutSession(Usuario usuario, String priceId, String successUrl, String cancelUrl) {
        try {
            // Certifique-se de que o usuário tem um customerId do Stripe
            String customerId = usuario.getStripeCustomerId();
            if (customerId == null || customerId.isEmpty()) {
                customerId = criarCustomer(usuario);
                usuario.setStripeCustomerId(customerId);
            }

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setPrice(priceId)
                    .setQuantity(1L)
                    .build();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setCustomer(customerId)
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .addLineItem(lineItem)
                    .build();

            com.stripe.model.checkout.Session session = com.stripe.model.checkout.Session.create(params);
            return session.getUrl();
        } catch (StripeException e) {
            log.error("Erro ao criar checkout session no Stripe", e);
            throw new RuntimeException("Erro ao criar checkout session no Stripe: " + e.getMessage(), e);
        }
    }

    @Override
    public String criarSubscription(Usuario usuario, String priceId) {
        try {
            // Certifique-se de que o usuário tem um customerId do Stripe
            String customerId = usuario.getStripeCustomerId();
            if (customerId == null || customerId.isEmpty()) {
                customerId = criarCustomer(usuario);
                usuario.setStripeCustomerId(customerId);
            }

            // Verificar se o cliente tem um método de pagamento padrão
            Customer customer = Customer.retrieve(customerId);
            String defaultPaymentMethod = customer.getInvoiceSettings() != null ? 
                    customer.getInvoiceSettings().getDefaultPaymentMethod() : null;

            if (defaultPaymentMethod == null || defaultPaymentMethod.isEmpty()) {
                // Se não tiver método de pagamento, criar uma sessão de checkout para coletar o método de pagamento
                log.warn("Cliente {} não tem método de pagamento padrão. Criando uma sessão de checkout.", customerId);
                
                // Criar uma assinatura de teste sem cobrança real (modo de teste)
                SubscriptionCreateParams.PaymentSettings paymentSettings = SubscriptionCreateParams.PaymentSettings.builder()
                        .setSaveDefaultPaymentMethod(SubscriptionCreateParams.PaymentSettings.SaveDefaultPaymentMethod.ON_SUBSCRIPTION)
                        .build();
                
                SubscriptionCreateParams.Item item = SubscriptionCreateParams.Item.builder()
                        .setPrice(priceId)
                        .build();

                SubscriptionCreateParams params = SubscriptionCreateParams.builder()
                        .setCustomer(customerId)
                        .addItem(item)
                        .setPaymentSettings(paymentSettings)
                        .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
                        .setTrialPeriodDays(7L) // Período de teste para evitar cobrança imediata
                        .build();

                Subscription subscription = Subscription.create(params);
                return subscription.getId();
            } else {
                // Se já tiver método de pagamento, criar a assinatura normalmente
                SubscriptionCreateParams.Item item = SubscriptionCreateParams.Item.builder()
                        .setPrice(priceId)
                        .build();

                SubscriptionCreateParams params = SubscriptionCreateParams.builder()
                        .setCustomer(customerId)
                        .addItem(item)
                        .build();

                Subscription subscription = Subscription.create(params);
                return subscription.getId();
            }
        } catch (StripeException e) {
            log.error("Erro ao criar assinatura no Stripe", e);
            throw new RuntimeException("Erro ao criar assinatura no Stripe: " + e.getMessage(), e);
        }
    }

    @Override
    public void cancelarSubscription(String subscriptionId) {
        try {
            Subscription subscription = Subscription.retrieve(subscriptionId);
            Map<String, Object> params = new HashMap<>();
            subscription.cancel(params);
        } catch (StripeException e) {
            log.error("Erro ao cancelar assinatura no Stripe", e);
            throw new RuntimeException("Erro ao cancelar assinatura no Stripe: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizarProduto(String produtoId, String nome, String descricao) {
        try {
            Product product = Product.retrieve(produtoId);
            Map<String, Object> params = new HashMap<>();
            params.put("name", nome);
            params.put("description", descricao);
            product.update(params);
            log.debug("Produto atualizado no Stripe: {}", produtoId);
        } catch (StripeException e) {
            log.error("Erro ao atualizar produto no Stripe", e);
            throw new RuntimeException("Erro ao atualizar produto no Stripe: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> processarWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            log.error("Erro ao verificar assinatura do webhook Stripe", e);
            throw new RuntimeException("Erro ao verificar assinatura do webhook Stripe: " + e.getMessage(), e);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("eventType", event.getType());
        response.put("eventId", event.getId());
        response.put("objectId", event.getDataObjectDeserializer().getRawJson());

        return response;
    }
}
