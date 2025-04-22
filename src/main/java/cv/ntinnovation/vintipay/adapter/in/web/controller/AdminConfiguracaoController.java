package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.ConfiguracaoPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.ConfiguracaoPagamentoWebMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.core.service.ApiSecretGeneratorService;
import cv.ntinnovation.vintipay.application.core.service.CriptografiaService;
import cv.ntinnovation.vintipay.application.port.in.AtualizaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.CriaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.SalvaConfiguracaoPagamentoPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    private final ApiSecretGeneratorService apiSecretGeneratorService;
    private final CriptografiaService criptografiaService;
    private final SalvaConfiguracaoPagamentoPort salvaConfiguracaoPagamentoPort;

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
    
    @GetMapping("/{id}/detalhes")
    public String detalhesConfiguracao(@PathVariable Long id, Model model) {
        ConfiguracaoPagamento configuracao = buscaConfiguracaoPagamentoUseCase.buscarPorId(id);
        model.addAttribute("configuracao", configuracao);
        return "admin/configuracoes/detalhes";
    }
    
    @PostMapping("/{id}/regenerate-secret")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> regenerateSecret(@PathVariable Long id) {
        try {
            // Buscar a configuração existente
            ConfiguracaoPagamento configuracao = buscaConfiguracaoPagamentoUseCase.buscarPorId(id);
            
            // Gerar nova API Secret
            String newApiSecret = apiSecretGeneratorService.generate();
            log.info("Nova API Secret gerada para configuração ID: {}", id);
            
            // Atualizar a configuração com a nova secret criptografada
            configuracao.setApiSecret(criptografiaService.criptografar(newApiSecret));
            configuracao.setUpdatedAt(LocalDateTime.now());
            
            // Salvar a configuração atualizada
            salvaConfiguracaoPagamentoPort.salvar(configuracao);
            
            // Retornar a nova secret (não criptografada) para exibição ao usuário
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "API Secret regenerada com sucesso");
            response.put("apiSecret", newApiSecret);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao regenerar API Secret", e);
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
        }
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
