package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.PapelRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.PapelResponse;
import cv.ntinnovation.vintipay.adapter.in.web.dto.PermissaoResponse;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.PapelWebMapper;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.PermissaoWebMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.Papel;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPapelUseCase;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPermissaoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin/papeis")
@RequiredArgsConstructor
public class PapelAdminController {

    private final GerenciaPapelUseCase gerenciaPapelUseCase;
    private final GerenciaPermissaoUseCase gerenciaPermissaoUseCase;
    private final PapelWebMapper papelWebMapper;
    private final PermissaoWebMapper permissaoWebMapper;

    @GetMapping
    public String listarPapeis(Model model) {
        List<PapelResponse> papeis = gerenciaPapelUseCase.listarTodos().stream()
                .map(papelWebMapper::toResponse)
                .collect(Collectors.toList());
        
        model.addAttribute("papeis", papeis);
        return "admin/papeis/lista";
    }

    @GetMapping("/novo")
    public String novoPapel(Model model) {
        model.addAttribute("papel", new PapelRequest());
        
        List<PermissaoResponse> permissoes = gerenciaPermissaoUseCase.listarTodas().stream()
                .map(permissaoWebMapper::toResponse)
                .collect(Collectors.toList());
        
        model.addAttribute("todasPermissoes", permissoes);
        return "admin/papeis/form";
    }

    @GetMapping("/{id}")
    public String editarPapel(@PathVariable Long id, Model model) {
        Papel papel = gerenciaPapelUseCase.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Papel não encontrado com ID: " + id));
        
        model.addAttribute("papel", papelWebMapper.toRequest(papel));
        
        List<PermissaoResponse> permissoes = gerenciaPermissaoUseCase.listarTodas().stream()
                .map(permissaoWebMapper::toResponse)
                .collect(Collectors.toList());
        
        model.addAttribute("todasPermissoes", permissoes);
        return "admin/papeis/form";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> salvarPapel(@RequestBody PapelRequest request) {
        try {
            log.info("Recebendo requisição para salvar papel: {}", request);
            
            Map<String, Object> response = new HashMap<>();
            Papel papel = papelWebMapper.toDomain(request);
            
            if (papel.getId() != null) {
                // Atualizar papel existente
                papel = gerenciaPapelUseCase.atualizar(papel.getId(), papel);
                response.put("success", true);
                response.put("message", "Papel atualizado com sucesso");
            } else {
                // Criar novo papel
                papel = gerenciaPapelUseCase.criar(papel);
                response.put("success", true);
                response.put("message", "Papel criado com sucesso");
            }
            
            response.put("id", papel.getId());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao salvar papel: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Erro ao salvar papel", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao salvar papel: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> excluirPapel(@PathVariable Long id) {
        try {
            gerenciaPapelUseCase.excluir(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Papel excluído com sucesso");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao excluir papel: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Erro ao excluir papel", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao excluir papel: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/verificar-codigo")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> verificarCodigoExistente(@RequestParam String codigo, @RequestParam(required = false) Long id) {
        boolean exists = gerenciaPapelUseCase.existePorCodigo(codigo);
        
        // Se estiver editando um papel existente, verificar se o código pertence a ele mesmo
        if (exists && id != null) {
            exists = gerenciaPapelUseCase.buscarPorCodigo(codigo)
                    .map(papel -> !papel.getId().equals(id))
                    .orElse(true);
        }
        
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    @PostMapping("/{papelId}/permissoes/{permissaoId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> adicionarPermissao(@PathVariable Long papelId, @PathVariable Long permissaoId) {
        try {
            Papel papel = gerenciaPapelUseCase.adicionarPermissao(papelId, permissaoId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Permissão adicionada com sucesso");
            response.put("papel", papelWebMapper.toResponse(papel));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao adicionar permissão: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Erro ao adicionar permissão", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao adicionar permissão: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @DeleteMapping("/{papelId}/permissoes/{permissaoId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removerPermissao(@PathVariable Long papelId, @PathVariable Long permissaoId) {
        try {
            Papel papel = gerenciaPapelUseCase.removerPermissao(papelId, permissaoId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Permissão removida com sucesso");
            response.put("papel", papelWebMapper.toResponse(papel));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao remover permissão: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Erro ao remover permissão", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao remover permissão: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
