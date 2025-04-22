package cv.ntinnovation.vintipay.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class BaseTestConfig {
    // Configurações comuns para testes podem ser adicionadas aqui
}
