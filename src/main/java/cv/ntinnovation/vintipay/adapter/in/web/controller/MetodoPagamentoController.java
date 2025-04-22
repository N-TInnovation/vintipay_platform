package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.MetodoPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.MetodoPagamentoResponse;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.MetodoPagamentoWebMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.MetodoPagamento;
import cv.ntinnovation.vintipay.application.port.in.BuscaMetodoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.CriaMetodoPagamentoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metodos-pagamento")
@RequiredArgsConstructor
public class MetodoPagamentoController {

    private final CriaMetodoPagamentoUseCase criaMetodoPagamentoUseCase;
    private final BuscaMetodoPagamentoUseCase buscaMetodoPagamentoUseCase;
    private final MetodoPagamentoWebMapper mapper;

    @PostMapping
    public ResponseEntity<MetodoPagamentoResponse> criar(@Valid @RequestBody MetodoPagamentoRequest request) {
        MetodoPagamento metodoPagamento = mapper.toMetodoPagamento(request);
        metodoPagamento = criaMetodoPagamentoUseCase.criar(metodoPagamento);
        return ResponseEntity.ok(mapper.toResponse(metodoPagamento));
    }

    @GetMapping
    public ResponseEntity<List<MetodoPagamentoResponse>> listar() {
        List<MetodoPagamento> metodos = buscaMetodoPagamentoUseCase.listarTodos();
        return ResponseEntity.ok(metodos.stream()
                .map(mapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetodoPagamentoResponse> buscarPorId(@PathVariable Long id) {
        return buscaMetodoPagamentoUseCase.buscarPorId(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
