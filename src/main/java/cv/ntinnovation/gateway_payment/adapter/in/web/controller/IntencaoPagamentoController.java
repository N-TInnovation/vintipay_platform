package cv.ntinnovation.gateway_payment.adapter.in.web.controller;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.IntencaoPagamentoRequest;
import cv.ntinnovation.gateway_payment.adapter.in.web.dto.IntencaoPagamentoResponse;
import cv.ntinnovation.gateway_payment.adapter.in.web.mapper.IntencaoPagamentoWebMapper;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.enums.IntencaoPagamentoEnum;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaConfiguracaoPagamentoAtivaUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.CriaContaPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.CriaIntencaoPagamentoUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/pagamentos")
public class IntencaoPagamentoController {

    private final CriaIntencaoPagamentoUseCase criaIntencaoPagamentoUseCase;
    private final CriaContaPagamentoUseCase criaContaPagamentoUseCase;
    private final IntencaoPagamentoWebMapper intencaoPagamentoWebMapper;
    private final BuscaConfiguracaoPagamentoAtivaUseCase buscaConfiguracaoPagamentoAtivaUseCase;

    public IntencaoPagamentoController(CriaIntencaoPagamentoUseCase criaIntencaoPagamentoUseCase,
                                      CriaContaPagamentoUseCase criaContaPagamentoUseCase,
                                      IntencaoPagamentoWebMapper intencaoPagamentoWebMapper,
                                      BuscaConfiguracaoPagamentoAtivaUseCase buscaConfiguracaoPagamentoAtivaUseCase) {
        this.criaIntencaoPagamentoUseCase = criaIntencaoPagamentoUseCase;
        this.criaContaPagamentoUseCase = criaContaPagamentoUseCase;
        this.intencaoPagamentoWebMapper = intencaoPagamentoWebMapper;
        this.buscaConfiguracaoPagamentoAtivaUseCase = buscaConfiguracaoPagamentoAtivaUseCase;
    }

    @PostMapping("/intencao/{intencao}")
    public ResponseEntity<IntencaoPagamentoResponse> criarIntencaoPagamento(
            @PathVariable String intencao,
            @RequestHeader("X-Client-Id") String clientId,
            @Valid @RequestBody IntencaoPagamentoRequest request) {
        try {

            IntencaoPagamentoEnum intencaoPagamentoEnum = IntencaoPagamentoEnum.findByIntencaoPagamento(intencao);
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
                request.getBillAddrPostCode()
            );
            
            // Associar a conta de pagamento à intenção
            intencaoPagamento.setContaPagamento(contaPagamento);
            
            // Criar a intenção de pagamento
            IntencaoPagamento intencaoPagamentoCriada = criaIntencaoPagamentoUseCase.criar(intencaoPagamento);
            
            // Converter para response e retornar
            IntencaoPagamentoResponse response = intencaoPagamentoWebMapper.toIntencaoPagamentoResponse(intencaoPagamentoCriada);
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
