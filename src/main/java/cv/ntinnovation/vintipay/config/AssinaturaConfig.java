package cv.ntinnovation.vintipay.config;

import cv.ntinnovation.vintipay.application.port.in.BuscaAssinaturaDirectaUseCase;
import cv.ntinnovation.vintipay.application.port.in.BuscaHistoricoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.GerenciaAssinaturaUseCase;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPlanoUseCase;
import cv.ntinnovation.vintipay.application.port.in.StripeIntegrationUseCase;
import cv.ntinnovation.vintipay.adapter.in.web.controller.UserAssinaturaController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuração para os componentes relacionados a assinaturas
 */
@Configuration
public class AssinaturaConfig {

    /**
     * Registra o controlador de assinatura do usuário como um bean explícito
     * para garantir que todas as dependências sejam injetadas corretamente
     */
    @Bean
    @Primary
    public UserAssinaturaController userAssinaturaController(
            BuscaHistoricoPagamentoUseCase buscaHistoricoPagamentoUseCase,
            GerenciaAssinaturaUseCase gerenciaAssinaturaUseCase,
            GerenciaPlanoUseCase gerenciaPlanoUseCase,
            BuscaAssinaturaDirectaUseCase buscaAssinaturaDirectaUseCase,
            StripeIntegrationUseCase stripeIntegrationUseCase) {
        
        return new UserAssinaturaController(
                buscaHistoricoPagamentoUseCase,
                gerenciaAssinaturaUseCase,
                gerenciaPlanoUseCase,
                buscaAssinaturaDirectaUseCase,
                stripeIntegrationUseCase
        );
    }
}
