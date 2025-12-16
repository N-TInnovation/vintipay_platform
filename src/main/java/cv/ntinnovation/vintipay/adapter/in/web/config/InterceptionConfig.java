package cv.ntinnovation.vintipay.adapter.in.web.config;

import cv.ntinnovation.vintipay.adapter.in.web.handler.VerificationInterceptorHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class InterceptionConfig implements WebMvcConfigurer {

    private final VerificationInterceptorHandler verificationInterceptorHandler;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(verificationInterceptorHandler)
                .addPathPatterns("/dashboard/**", "/user/**") // rotas protegidas
                .excludePathPatterns("/", "/login", "/register", "/user/tenant/create"); // exceções
    }
}
