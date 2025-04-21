package cv.ntinnovation.gateway_payment.adapter.in.web.controller;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.ConfiguracaoPagamentoRequest;
import cv.ntinnovation.gateway_payment.adapter.in.web.mapper.ConfiguracaoPagamentoWebMapper;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.gateway_payment.application.port.in.AtualizaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.CriaConfiguracaoPagamentoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/admin/configuracoes")
@RequiredArgsConstructor
public class AdminConfiguracaoController {

    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoUseCase;
    private final CriaConfiguracaoPagamentoUseCase criaConfiguracaoPagamentoUseCase;
    private final AtualizaConfiguracaoPagamentoUseCase atualizaConfiguracaoPagamentoUseCase;
    private final ConfiguracaoPagamentoWebMapper configuracaoPagamentoWebMapper;
    private final FingerPrintService fingerPrintService;

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
    public ResponseEntity<Map<String, Object>> salvarConfiguracao(@RequestBody @Valid ConfiguracaoPagamentoRequest request) {
        try {
            ConfiguracaoPagamento configuracao = configuracaoPagamentoWebMapper.toDomain(request);
            
            if (configuracao.getId() == null) {
                // Gera returnAutCode automaticamente para novas configurações
                String returnAutCode = generateReturnAutCode();
                log.info("ReturnAutCode gerado: {}", returnAutCode);
                configuracao.setReturnAutCode(returnAutCode);
                
                ConfiguracaoPagamento configuracaoSalva = criaConfiguracaoPagamentoUseCase.criar(configuracao);
                
                // Usar HashMap em vez de Map.of para mais de 5 pares de chave-valor
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Configuração salva com sucesso");
                response.put("apiKey", configuracaoSalva.getApiKey());
                response.put("apiSecret", configuracaoSalva.getApiSecret());
                response.put("returnAutCode", configuracaoSalva.getReturnAutCode());
                
                return ResponseEntity.ok(response);
            } else {
                atualizaConfiguracaoPagamentoUseCase.atualizar(configuracao);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Configuração atualizada com sucesso"
                ));
            }
        } catch (Exception e) {
            log.error("Erro ao salvar configuração", e);
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
        }
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
    
    /**
     * Gera um código de autenticação para retorno seguro
     * Combina UUID aleatório com hash para garantir unicidade e segurança
     */
    private String generateReturnAutCode() {
        String uuid = UUID.randomUUID().toString();
        // Usar o serviço de fingerprint para gerar um hash seguro
        return fingerPrintService.generateHmac(uuid, "RETURN_AUTH_" + System.currentTimeMillis());
    }
}
