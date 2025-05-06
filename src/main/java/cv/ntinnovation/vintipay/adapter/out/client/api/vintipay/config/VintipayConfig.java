package cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.config;

import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.VintipayAuth;
import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

public class VintipayConfig {
    @Bean
    public RequestInterceptor requestInterceptor(){
        return new VintipayHeaderConfig();
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
