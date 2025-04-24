package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.ConfiguracaoPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.ConfiguracaoPagamentoWebMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.core.service.ApiSecretGeneratorService;
import cv.ntinnovation.vintipay.application.core.service.CriptografiaService;
import cv.ntinnovation.vintipay.application.port.out.ConsultaConfiguracaoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.in.AtualizaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.CriaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.SalvaConfiguracaoPagamentoPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/admin/configuracoes")
@RequiredArgsConstructor
public class AdminConfiguracaoController {

    private final BuscaConfiguracaoPagamentoPort buscaConfiguracaoPagamentoPort;
    private final CriaConfiguracaoPagamentoUseCase criaConfiguracaoPagamentoUseCase;
    private final AtualizaConfiguracaoPagamentoUseCase atualizaConfiguracaoPagamentoUseCase;
    private final ConfiguracaoPagamentoWebMapper configuracaoPagamentoWebMapper;
    private final FingerPrintService fingerPrintService;
    private final ApiSecretGeneratorService apiSecretGeneratorService;
    private final CriptografiaService criptografiaService;
    private final SalvaConfiguracaoPagamentoPort salvaConfiguracaoPagamentoPort;
    private final ConsultaConfiguracaoPagamentoPort consultaConfiguracaoPagamentoPort;

    @GetMapping
    public String listarConfiguracoes(Model model) {
        List<ConfiguracaoPagamento> configuracoes = buscaConfiguracaoPagamentoPort.buscarTodas();
        model.addAttribute("configuracoes", configuracoes);
        return "admin/configuracoes/lista";
    }

    @GetMapping("/nova")
    public String novaConfiguracao(@RequestParam(required = false) Long usuario, Model model) {
        ConfiguracaoPagamentoRequest configuracaoRequest = new ConfiguracaoPagamentoRequest();
        
        // Se o ID do usuário foi fornecido, associar à configuração
        if (usuario != null) {
            configuracaoRequest.setUsuarioId(usuario);
            log.debug("Criando nova configuração para o usuário ID: {}", usuario);
        }
        
        model.addAttribute("configuracao", configuracaoRequest);
        model.addAttribute("usuarioId", usuario);
        return "admin/configuracoes/form";
    }

    @GetMapping("/{id}")
    public String editarConfiguracao(@PathVariable Long id, Model model) {
        Optional<ConfiguracaoPagamento> configuracaoOpt = buscaConfiguracaoPagamentoPort.buscarPorId(id);
        
        if (configuracaoOpt.isPresent()) {
            model.addAttribute("configuracao", configuracaoPagamentoWebMapper.toResponse(configuracaoOpt.get()));
            return "admin/configuracoes/form";
        } else {
            // Configuração não encontrada, redirecionar para a lista com mensagem de erro
            return "redirect:/admin/configuracoes?erro=Configuração não encontrada";
        }
    }
    
    @GetMapping("/{id}/editar")
    public String editarConfiguracaoAlternativo(@PathVariable Long id, Model model) {
        // Redirecionar para o endpoint principal de edição
        return editarConfiguracao(id, model);
    }
    
    @GetMapping("/{id}/detalhes")
    public String detalhesConfiguracao(@PathVariable Long id, Model model) {
        Optional<ConfiguracaoPagamento> configuracaoOpt = buscaConfiguracaoPagamentoPort.buscarPorId(id);
        
        if (configuracaoOpt.isPresent()) {
            model.addAttribute("configuracao", configuracaoOpt.get());
            return "admin/configuracoes/detalhes";
        } else {
            // Configuração não encontrada, redirecionar para a lista com mensagem de erro
            return "redirect:/admin/configuracoes?erro=Configuração não encontrada";
        }
    }
    
    @GetMapping("/verificar-posid")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> verificarPosIdExistente(@RequestParam String posId) {
        boolean exists = consultaConfiguracaoPagamentoPort.existePorPosId(posId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    @PostMapping("/{id}/regenerate-secret")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> regenerateSecret(@PathVariable Long id) {
        try {
            // Buscar a configuração existente
            Optional<ConfiguracaoPagamento> configuracaoOpt = buscaConfiguracaoPagamentoPort.buscarPorId(id);
            
            if (!configuracaoOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Configuração não encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            ConfiguracaoPagamento configuracao = configuracaoOpt.get();
            
            // Gerar nova API Secret
            String newApiSecret = apiSecretGeneratorService.generate();
            log.info("Nova API Secret gerada para a configuração ID: {}", id);
            
            // Criptografar a nova API Secret
            String apiSecretCriptografado = criptografiaService.criptografar(newApiSecret);
            log.info("Nova API Secret criptografada");
            
            // Atualizar a configuração com a nova API Secret
            configuracao.setApiSecret(apiSecretCriptografado);
            configuracao.setUpdatedAt(LocalDateTime.now());
            
            // Salvar a configuração atualizada
            salvaConfiguracaoPagamentoPort.salvar(configuracao);
            log.info("Configuração atualizada com nova API Secret. ID: {}", id);
            
            // Retornar a nova API Secret (não criptografada) para o cliente
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "API Secret regenerada com sucesso");
            response.put("apiSecret", newApiSecret);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao regenerar API Secret", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao regenerar API Secret: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> salvarConfiguracao(@RequestBody ConfiguracaoPagamentoRequest request) {
        try {
            log.info("Recebendo requisição para salvar configuração: {}", request);
            
            // Verificar se o POS ID já existe
            if (request.getPosId() != null) {
                log.info("Verificando existência do POS ID no controlador: {}", request.getPosId());
                
                // Verificar se é uma edição de configuração existente com o mesmo POS ID (permitido)
                boolean isEditingSamePosId = false;
                if (request.getId() != null) {
                    Optional<ConfiguracaoPagamento> existingConfig = buscaConfiguracaoPagamentoPort.buscarPorId(request.getId());
                    if (existingConfig.isPresent()) {
                        String existingPosId = existingConfig.get().getPosId();
                        isEditingSamePosId = request.getPosId().equals(existingPosId);
                        log.info("Editando configuração existente: ID={}, POS ID atual={}, POS ID novo={}, Mesmo POS ID={}",
                                request.getId(), existingPosId, request.getPosId(), isEditingSamePosId);
                    }
                }
                
                // Verificar se o POS ID já existe para o mesmo usuário
                boolean posIdExistsForSameUser = false;
                if (request.getUsuarioId() != null) {
                    posIdExistsForSameUser = consultaConfiguracaoPagamentoPort.existePorPosIdEUsuario(
                            request.getPosId(), request.getUsuarioId());
                    log.info("POS ID existe para o mesmo usuário? {}", posIdExistsForSameUser);
                }
                
                // Bloquear se o POS ID existe para o mesmo usuário e não estamos editando a mesma configuração
                boolean shouldBlockSave = posIdExistsForSameUser && !isEditingSamePosId;
                log.info("Deve bloquear o salvamento? {}", shouldBlockSave);
                
                if (shouldBlockSave) {
                    log.warn("Tentativa de criar/editar configuração com POS ID já existente para o mesmo usuário: {}", request.getPosId());
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Você já possui uma configuração com este POS ID: " + request.getPosId());
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            }
            
            ConfiguracaoPagamento configuracao = configuracaoPagamentoWebMapper.toDomain(request);
            log.info("Configuração mapeada para domínio: {}", configuracao);
            
            Map<String, Object> response = new HashMap<>();
            
            if (configuracao.getId() != null) {
                // Atualizar configuração existente
                configuracao = atualizaConfiguracaoPagamentoUseCase.atualizar(configuracao);
                response.put("success", true);
                response.put("message", "Configuração atualizada com sucesso");
                response.put("id", configuracao.getId());
            } else {
                // Verificar se o usuário foi informado
                if (configuracao.getUsuarioId() == null) {
                    log.warn("Tentativa de criar configuração sem usuário associado");
                    response.put("success", false);
                    response.put("message", "É necessário informar um usuário para a configuração");
                    return ResponseEntity.badRequest().body(response);
                }
                
                // Gerar returnAutCode automaticamente se não foi fornecido
                if (configuracao.getReturnAutCode() == null || configuracao.getReturnAutCode().isEmpty()) {
                    String returnAutCode = generateReturnAutCode();
                    log.info("ReturnAutCode gerado: {}", returnAutCode);
                    configuracao.setReturnAutCode(returnAutCode);
                }
                
                // Criar nova configuração
                configuracao = criaConfiguracaoPagamentoUseCase.criar(configuracao);
                response.put("success", true);
                response.put("message", "Configuração criada com sucesso");
                response.put("id", configuracao.getId());
                response.put("apiKey", configuracao.getApiKey());
                response.put("apiSecret", configuracao.getApiSecret());
                response.put("returnAutCode", configuracao.getReturnAutCode());
            }
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Capturar exceção específica para POS ID duplicado ou validação de usuário
            log.warn("Erro de validação: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (DataIntegrityViolationException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            
            // Verificar se é um erro de duplicação de POS ID
            if (e.getMessage() != null && 
                (e.getMessage().contains("UK_POS_ID") || 
                 e.getMessage().contains("UKiq9p0eeolwpce4njfkxd6vhpn") || 
                 e.getMessage().contains("Duplicate entry") && e.getMessage().contains("pos_id"))) {
                log.error("Erro de duplicação de POS ID: {}", e.getMessage());
                response.put("message", "Já existe uma configuração com este POS ID. Por favor, utilize outro valor.");
            } else {
                log.error("Erro de integridade de dados: {}", e.getMessage());
                response.put("message", "Erro de integridade de dados: " + e.getMessage());
            }
            
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            log.error("Erro ao salvar configuração: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao processar a requisição: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
