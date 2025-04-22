package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.ConfiguracaoPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.ConfiguracaoPagamentoResponse;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaConfiguracaoPagamentoUsuarioUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/configuracoes-pagamento")
@RequiredArgsConstructor
public class ConfiguracaoPagamentoUsuarioController {

    private final GerenciaConfiguracaoPagamentoUsuarioUseCase gerenciaConfiguracaoPagamentoUsuarioUseCase;

    @GetMapping
    public String listarConfiguracoes(@AuthenticationPrincipal Usuario usuario, Model model) {
        List<ConfiguracaoPagamento> configuracoes = gerenciaConfiguracaoPagamentoUsuarioUseCase.buscarConfiguracoesDoUsuario(usuario.getId());
        model.addAttribute("configuracoes", configuracoes);
        return "configuracoes/lista";
    }

    @GetMapping("/nova")
    public String exibirFormularioNova(Model model) {
        model.addAttribute("configuracao", new ConfiguracaoPagamentoRequest());
        return "configuracoes/form";
    }

    @PostMapping("/nova")
    public String criarConfiguracao(
            @Valid @ModelAttribute("configuracao") ConfiguracaoPagamentoRequest request,
            BindingResult result,
            @AuthenticationPrincipal Usuario usuario,
            Model model) {
        
        if (result.hasErrors()) {
            return "configuracoes/form";
        }

        try {
            ConfiguracaoPagamento configuracao = mapearRequestParaEntidade(request);
            gerenciaConfiguracaoPagamentoUsuarioUseCase.criarConfiguracaoParaUsuario(configuracao, usuario.getId());
            return "redirect:/configuracoes-pagamento?sucesso";
        } catch (Exception e) {
            log.error("Erro ao criar configuração de pagamento", e);
            model.addAttribute("erro", "Erro ao criar configuração: " + e.getMessage());
            return "configuracoes/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String exibirFormularioEditar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario, Model model) {
        ConfiguracaoPagamento configuracao = gerenciaConfiguracaoPagamentoUsuarioUseCase
                .buscarConfiguracaoPorIdEUsuario(id, usuario.getId())
                .orElseThrow(() -> new RuntimeException("Configuração não encontrada"));
        
        model.addAttribute("configuracao", mapearEntidadeParaRequest(configuracao));
        return "configuracoes/form";
    }

    @PostMapping("/{id}/editar")
    public String atualizarConfiguracao(
            @PathVariable Long id,
            @Valid @ModelAttribute("configuracao") ConfiguracaoPagamentoRequest request,
            BindingResult result,
            @AuthenticationPrincipal Usuario usuario,
            Model model) {
        
        if (result.hasErrors()) {
            return "configuracoes/form";
        }

        try {
            ConfiguracaoPagamento configuracao = mapearRequestParaEntidade(request);
            gerenciaConfiguracaoPagamentoUsuarioUseCase.atualizarConfiguracaoParaUsuario(id, configuracao, usuario.getId());
            return "redirect:/configuracoes-pagamento?sucesso";
        } catch (Exception e) {
            log.error("Erro ao atualizar configuração de pagamento", e);
            model.addAttribute("erro", "Erro ao atualizar configuração: " + e.getMessage());
            return "configuracoes/form";
        }
    }

    @PostMapping("/{id}/ativar")
    public String ativarConfiguracao(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        gerenciaConfiguracaoPagamentoUsuarioUseCase.ativarConfiguracao(id, usuario.getId());
        return "redirect:/configuracoes-pagamento?sucesso";
    }

    @PostMapping("/{id}/desativar")
    public String desativarConfiguracao(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        gerenciaConfiguracaoPagamentoUsuarioUseCase.desativarConfiguracao(id, usuario.getId());
        return "redirect:/configuracoes-pagamento?sucesso";
    }

    // API REST para integração com outros sistemas

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<ConfiguracaoPagamentoResponse>> listarConfiguracoesApi(@AuthenticationPrincipal Usuario usuario) {
        List<ConfiguracaoPagamento> configuracoes = gerenciaConfiguracaoPagamentoUsuarioUseCase.buscarConfiguracoesDoUsuario(usuario.getId());
        List<ConfiguracaoPagamentoResponse> response = configuracoes.stream()
                .map(this::mapearEntidadeParaResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<ConfiguracaoPagamentoResponse> criarConfiguracaoApi(
            @Valid @RequestBody ConfiguracaoPagamentoRequest request,
            @AuthenticationPrincipal Usuario usuario) {
        
        ConfiguracaoPagamento configuracao = mapearRequestParaEntidade(request);
        ConfiguracaoPagamento configuracaoSalva = gerenciaConfiguracaoPagamentoUsuarioUseCase.criarConfiguracaoParaUsuario(configuracao, usuario.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapearEntidadeParaResponse(configuracaoSalva));
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ConfiguracaoPagamentoResponse> atualizarConfiguracaoApi(
            @PathVariable Long id,
            @Valid @RequestBody ConfiguracaoPagamentoRequest request,
            @AuthenticationPrincipal Usuario usuario) {
        
        ConfiguracaoPagamento configuracao = mapearRequestParaEntidade(request);
        ConfiguracaoPagamento configuracaoAtualizada = gerenciaConfiguracaoPagamentoUsuarioUseCase.atualizarConfiguracaoParaUsuario(id, configuracao, usuario.getId());
        return ResponseEntity.ok(mapearEntidadeParaResponse(configuracaoAtualizada));
    }

    // Métodos auxiliares para mapeamento

    private ConfiguracaoPagamento mapearRequestParaEntidade(ConfiguracaoPagamentoRequest request) {
        return ConfiguracaoPagamento.builder()
                .posId(request.getPosId())
                .merchantId(request.getMerchantId())
                .terminalId(request.getTerminalId())
                .apiKey(request.getApiKey())
                .apiSecret(request.getApiSecret())
                .posAutCode(request.getPosAutCode())
                .returnAutCode(request.getReturnAutCode())
                .returnUrl(request.getReturnUrl())
                .nomeLoja(request.getNomeLoja())
                .urlLoja(request.getUrlLoja())
                .telefone(request.getTelefone())
                .email(request.getEmail())
                .isActive(true)
                .build();
    }

    private ConfiguracaoPagamentoRequest mapearEntidadeParaRequest(ConfiguracaoPagamento configuracao) {
        ConfiguracaoPagamentoRequest request = new ConfiguracaoPagamentoRequest();
        request.setPosId(configuracao.getPosId());
        request.setMerchantId(configuracao.getMerchantId());
        request.setTerminalId(configuracao.getTerminalId());
        request.setApiKey(configuracao.getApiKey());
        request.setApiSecret(configuracao.getApiSecret());
        request.setPosAutCode(configuracao.getPosAutCode());
        request.setReturnAutCode(configuracao.getReturnAutCode());
        request.setReturnUrl(configuracao.getReturnUrl());
        request.setNomeLoja(configuracao.getNomeLoja());
        request.setUrlLoja(configuracao.getUrlLoja());
        request.setTelefone(configuracao.getTelefone());
        request.setEmail(configuracao.getEmail());
        return request;
    }

    private ConfiguracaoPagamentoResponse mapearEntidadeParaResponse(ConfiguracaoPagamento configuracao) {
        return ConfiguracaoPagamentoResponse.builder()
                .id(configuracao.getId())
                .posId(configuracao.getPosId())
                .merchantId(configuracao.getMerchantId())
                .terminalId(configuracao.getTerminalId())
                .apiKey(configuracao.getApiKey())
                .posAutCode(configuracao.getPosAutCode())
                .returnAutCode(configuracao.getReturnAutCode())
                .returnUrl(configuracao.getReturnUrl())
                .nomeLoja(configuracao.getNomeLoja())
                .urlLoja(configuracao.getUrlLoja())
                .telefone(configuracao.getTelefone())
                .email(configuracao.getEmail())
                .ativo(configuracao.getIsActive())
                .build();
    }
}
