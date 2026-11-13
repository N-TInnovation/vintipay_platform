package cv.ntinnovation.vintipay.adapter.in.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import cv.ntinnovation.vintipay.adapter.in.web.handler.VerificationInterceptorHandler;
import cv.ntinnovation.vintipay.application.core.service.AmbienteService;
import cv.ntinnovation.vintipay.application.port.out.GerenciaTenantPort;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GerenciaTenantPort gerenciaTenantPort;

    @MockitoBean
    private VerificationInterceptorHandler verificationInterceptorHandler;

    @MockitoBean
    private AmbienteService ambienteService;

    @Test
    void deveRedirecionarParaKeycloak() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/oauth2/authorization/keycloak"));
    }
}
