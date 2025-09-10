package cv.ntinnovation.vintipay.adapter.out.client.api.brevo.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

public class ConfigBrevo {
    @Bean
    public Retryer brevoRetryer() {
        return new Retryer.Default(5000,5000,3);
    }

    @Bean
    public Request.Options brevoOptions() {
        return new Request.Options(5L, TimeUnit.SECONDS,5L,TimeUnit.SECONDS, true);
    }

    @Bean
    public Logger.Level brevoLoggerLevel() {
        return Logger.Level.FULL;
    }
}
