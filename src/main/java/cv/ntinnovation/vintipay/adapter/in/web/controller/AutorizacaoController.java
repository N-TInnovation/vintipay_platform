package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.AutenticacaoResponse;
import cv.ntinnovation.vintipay.adapter.in.web.dto.CredencialsRequest;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.port.in.BuscaConfiguracaoPagamentoAtivaUseCase;
import cv.ntinnovation.vintipay.application.port.in.CriarAutorizacaoUseCase;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/authorize")
public class AutorizacaoController {
    private final CriarAutorizacaoUseCase criarAutorizacaoUseCase;
    private final BuscaConfiguracaoPagamentoAtivaUseCase buscaConfiguracaoPagamentoAtivaUseCase;

    @PostConstruct
    public void init() {
        log.info("AutorizacaoController inicializado com path: /api/v1/authorize");
    }

    @PostMapping( path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AutenticacaoResponse> login(@RequestBody CredencialsRequest credencialsRequest) {
        log.info("Recebida requisição de autorização para clientId: {}", credencialsRequest.getClientId());
        
        try {
            ConfiguracaoPagamento configuracao = buscaConfiguracaoPagamentoAtivaUseCase
                    .buscarPorCredenciais(credencialsRequest.getClientId(), credencialsRequest.getClientSecret());
            
            String token = criarAutorizacaoUseCase.getToken(configuracao.getMerchantId(), configuracao.getApiKey());
            return ResponseEntity.ok(AutenticacaoResponse.builder()
                    .accessToken(token)
                    .build());
        } catch (IllegalStateException e) {
            log.error("Erro ao autenticar: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AutenticacaoResponse.builder()
                            .error("Credenciais inválidas")
                            .build());
        }
    }

    @PostMapping("/logout")
    public String logout() {
        return "login-index.html";
    }
}
