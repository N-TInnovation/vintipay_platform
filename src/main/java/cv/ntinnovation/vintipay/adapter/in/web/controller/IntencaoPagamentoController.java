package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.IntencaoPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.IntencaoPagamentoResponse;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.IntencaoPagamentoWebMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.IntencaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.port.in.BuscaConfiguracaoPagamentoAtivaUseCase;
import cv.ntinnovation.vintipay.application.port.in.CriaContaPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.CriaIntencaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.CriaSessaoPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/pagamentos")
@RequiredArgsConstructor
public class IntencaoPagamentoController {

    private final CriaIntencaoPagamentoUseCase criaIntencaoPagamentoUseCase;
    private final CriaContaPagamentoUseCase criaContaPagamentoUseCase;
    private final IntencaoPagamentoWebMapper intencaoPagamentoWebMapper;
    private final BuscaConfiguracaoPagamentoAtivaUseCase buscaConfiguracaoPagamentoAtivaUseCase;
    private final CriaSessaoPagamentoUseCase criaSessaoPagamentoUseCase;

    private static final String SESSION_COOKIE_NAME = "PAYMENT_SESSION_ID";
    private static final int SESSION_COOKIE_MAX_AGE = 1800; // 30 minutos em segundos

    @PostMapping("/intencao/{intencao}")
    public ResponseEntity<IntencaoPagamentoResponse> criarIntencaoPagamento(
            @PathVariable String intencao,
            @RequestHeader("X-Client-Id") String clientId,
            @Valid @RequestBody IntencaoPagamentoRequest request,
            Principal principal,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        try {

            if (!principal.getName().equals(clientId)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            IntencaoPagamentoEnum intencaoPagamentoEnum = IntencaoPagamentoEnum.findByIntencaoPagamento(intencao.trim());
            if (intencaoPagamentoEnum == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            // Buscar e validar configuração pelo clientId
            ConfiguracaoPagamento configuracao = buscaConfiguracaoPagamentoAtivaUseCase
                    .buscarPorClientId(clientId);
            
            // Converter o request para o domínio
            IntencaoPagamento intencaoPagamento = intencaoPagamentoWebMapper.toIntencaoPagamento(request);
            
            // Definir o posId da configuração
            intencaoPagamento.setPosId(configuracao.getPosId());

            intencaoPagamento.setTransactionCode(intencaoPagamentoEnum.getTransactionCode());

            // Criar ou buscar a conta de pagamento pelo email
            ContaPagamento contaPagamento = criaContaPagamentoUseCase.criarOuBuscarPorEmail(
                request.getEmail(),
                request.getBillAddrCity(),
                request.getBillAddrCountry(),
                request.getBillAddrLine1(),
                request.getBillAddrPostCode(),
                request.getPhoneCode(),
                request.getPhoneNumber()
            );
            
            // Associar a conta de pagamento à intenção
            intencaoPagamento.setContaPagamento(contaPagamento);
            
            // Criar a intenção de pagamento
            IntencaoPagamento intencaoPagamentoCriada = criaIntencaoPagamentoUseCase.criar(intencaoPagamento);
            
            // Criar uma sessão de pagamento para esta intenção
            SessaoPagamento sessao = criaSessaoPagamentoUseCase.criar(
                    intencaoPagamentoCriada.getReferencia(),
                    clientId,
                    servletRequest
            );
            
            // Adicionar cookie de sessão na resposta
            Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, sessao.getId());
            sessionCookie.setMaxAge(SESSION_COOKIE_MAX_AGE);
            sessionCookie.setPath("/");
            sessionCookie.setHttpOnly(true);
            sessionCookie.setSecure(true); // Somente HTTPS
            servletResponse.addCookie(sessionCookie);
            
            // Converter para response e retornar
            IntencaoPagamentoResponse response = intencaoPagamentoWebMapper.toIntencaoPagamentoResponse(intencaoPagamentoCriada);
            // Adicionar o ID da sessão à resposta
            response.setSessionId(sessao.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/intencao/{id}")
    public ResponseEntity<String> getIntencaoPagamento(@PathVariable Long id) {
        // Implementação futura para buscar intenção de pagamento por ID
        return ResponseEntity.ok("Implementação futura para buscar intenção de pagamento com ID: " + id);
    }
    
    @GetMapping("/intencao/referencia/{reference}")
    public ResponseEntity<String> getIntencaoPagamentoByReference(@PathVariable String reference) {
        // Implementação futura para buscar intenção de pagamento por referência
        return ResponseEntity.ok("Implementação futura para buscar intenção de pagamento com referência: " + reference);
    }
}
