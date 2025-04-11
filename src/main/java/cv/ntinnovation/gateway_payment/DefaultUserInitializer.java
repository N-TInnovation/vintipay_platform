package cv.ntinnovation.gateway_payment;

import cv.ntinnovation.gateway_payment.adapter.out.db.repository.ConfiguracaoPagamentoRepository;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.MetodoPagamento;
import cv.ntinnovation.gateway_payment.application.port.in.CriaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.CriaMetodoPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultUserInitializer implements CommandLineRunner {

    private final ConfiguracaoPagamentoRepository configuracaoPagamentoRepository;
    private final CriaMetodoPagamentoUseCase criaMetodoPagamentoUseCase;
    private final CriaConfiguracaoPagamentoUseCase criaConfiguracaoPagamentoUseCase;

    @Override
    @Transactional
    public void run(String... args) {
        /*if (configuracaoPagamentoRepository.count() > 0) {
            log.info("Configurações já existem, pulando inicialização");
            return;
        }

        log.info("Iniciando cadastro das configurações padrão");

        // Criar método de pagamento padrão
        MetodoPagamento metodoPagamento = new MetodoPagamento();
        metodoPagamento.setName("CARTAO_24");
        metodoPagamento = criaMetodoPagamentoUseCase.criar(metodoPagamento);

        // Criar configuração de pagamento padrão
        ConfiguracaoPagamento configuracao =  ConfiguracaoPagamento.builder().build();
        configuracao.setPosId("POS789");
        configuracao.setMerchantId("MERCHANT001");
        configuracao.setTerminalId("TERM001");
        configuracao.setApiKey("key123");
        configuracao.setApiSecret("secret456");

        criaConfiguracaoPagamentoUseCase.criar(configuracao);

        log.info("Configurações padrão cadastradas com sucesso");*/
    }
}
