package cv.ntinnovation.gateway_payment.adapter.in.web.controller;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.ConfiguracaoPagamentoRequest;
import cv.ntinnovation.gateway_payment.adapter.in.web.dto.ConfiguracaoPagamentoResponse;
import cv.ntinnovation.gateway_payment.adapter.in.web.mapper.ConfiguracaoPagamentoWebMapper;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.port.in.CriaConfiguracaoPagamentoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/configuracoes-pagamento")
@RequiredArgsConstructor
public class ConfiguracaoPagamentoController {

    private final CriaConfiguracaoPagamentoUseCase criaConfiguracaoPagamentoUseCase;
    private final ConfiguracaoPagamentoWebMapper configuracaoPagamentoWebMapper;

    @PostMapping
    public ResponseEntity<ConfiguracaoPagamentoResponse> criar(@RequestBody @Valid ConfiguracaoPagamentoRequest request) {
        ConfiguracaoPagamento configuracao = configuracaoPagamentoWebMapper.toConfiguracao(request);
        ConfiguracaoPagamento configuracaoCriada = criaConfiguracaoPagamentoUseCase.criar(configuracao);
        return ResponseEntity.ok(configuracaoPagamentoWebMapper.toResponse(configuracaoCriada));
    }
}
