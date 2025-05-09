package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.ContaPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.ContaPagamentoResponse;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.ContaPagamentoWebMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.vintipay.application.port.in.CriaContaPagamentoUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/contas-pagamento")
public class ContaPagamentoController {

    private final CriaContaPagamentoUseCase criaContaPagamentoUseCase;
    private final ContaPagamentoWebMapper contaPagamentoWebMapper;

    public ContaPagamentoController(CriaContaPagamentoUseCase criaContaPagamentoUseCase,
                                   ContaPagamentoWebMapper contaPagamentoWebMapper) {
        this.criaContaPagamentoUseCase = criaContaPagamentoUseCase;
        this.contaPagamentoWebMapper = contaPagamentoWebMapper;
    }

    @PostMapping
    public ResponseEntity<ContaPagamentoResponse> criarContaPagamento(@Valid @RequestBody ContaPagamentoRequest request) {
        // Criar ou buscar a conta de pagamento pelo email
        ContaPagamento contaPagamento = criaContaPagamentoUseCase.criarOuBuscarPorEmail(
            request.getName(),
            request.getEmail(),
            request.getBillAddrCity(),
            request.getBillAddrCountry(),
            request.getBillAddrLine1(),
            request.getBillAddrPostCode(),
            request.getPhoneCode(),
            request.getPhoneNumber()
        );
        
        // Converter para response e retornar
        ContaPagamentoResponse response = contaPagamentoWebMapper.toContaPagamentoResponse(contaPagamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<String> getContaPagamento(@PathVariable Long id) {
        // Implementação futura para buscar conta de pagamento por ID
        return ResponseEntity.ok("Implementação futura para buscar conta de pagamento com ID: " + id);
    }
}
