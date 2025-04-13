package cv.ntinnovation.gateway_payment.adapter.in.web.controller;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.ConfiguracaoPagamentoRequest;
import cv.ntinnovation.gateway_payment.adapter.in.web.mapper.ConfiguracaoPagamentoWebMapper;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.port.in.AtualizaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.CriaConfiguracaoPagamentoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/configuracoes")
@RequiredArgsConstructor
public class AdminConfiguracaoController {

    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoUseCase;
    private final CriaConfiguracaoPagamentoUseCase criaConfiguracaoPagamentoUseCase;
    private final AtualizaConfiguracaoPagamentoUseCase atualizaConfiguracaoPagamentoUseCase;
    private final ConfiguracaoPagamentoWebMapper configuracaoPagamentoWebMapper;

    @GetMapping
    public String listarConfiguracoes(Model model) {
        List<ConfiguracaoPagamento> configuracoes = buscaConfiguracaoPagamentoUseCase.buscarTodas();
        model.addAttribute("configuracoes", configuracoes);
        return "admin/configuracoes/lista";
    }

    @GetMapping("/nova")
    public String novaConfiguracao(Model model) {
        model.addAttribute("configuracao", new ConfiguracaoPagamentoRequest());
        return "admin/configuracoes/form";
    }

    @GetMapping("/{id}")
    public String editarConfiguracao(@PathVariable Long id, Model model) {
        ConfiguracaoPagamento configuracao = buscaConfiguracaoPagamentoUseCase.buscarPorId(id);
        model.addAttribute("configuracao", configuracaoPagamentoWebMapper.toResponse(configuracao));
        return "admin/configuracoes/form";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Void> salvarConfiguracao(@RequestBody @Valid ConfiguracaoPagamentoRequest request) {
        ConfiguracaoPagamento configuracao = configuracaoPagamentoWebMapper.toDomain(request);
        
        if (configuracao.getId() == null) {
            criaConfiguracaoPagamentoUseCase.criar(configuracao);
        } else {
            atualizaConfiguracaoPagamentoUseCase.atualizar(configuracao);
        }
        
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<Void> alterarStatus(@PathVariable Long id, @RequestParam Boolean ativo) {
        if (ativo) {
            atualizaConfiguracaoPagamentoUseCase.ativar(id);
        } else {
            atualizaConfiguracaoPagamentoUseCase.desativar(id);
        }
        return ResponseEntity.ok().build();
    }
}
