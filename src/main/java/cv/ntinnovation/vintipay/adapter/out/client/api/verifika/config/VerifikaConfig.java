package cv.ntinnovation.vintipay.adapter.out.client.api.verifika.config;

import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.VerifikaAuthClient;
import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.config.VintipayHeaderConfig;
import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

public class VerifikaConfig {

    @Bean
    public RequestInterceptor requestInterceptor(VerifikaAuthClient verifikaAuthClient){
        return new VerifikaHeaderConfig(verifikaAuthClient);
    }
    @Bean
    public Retryer retryer(){
        return new Retryer.Default(1000,1000,3);
    }

    @Bean
    public Request.Options options(){
        return new Request.Options(5L, TimeUnit.SECONDS,5L,TimeUnit.SECONDS, true);
    }

    @Bean
    public Logger.Level level(){
        return Logger.Level.FULL;
    }
}
