package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.PermissaoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.PermissaoResponse;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.PermissaoWebMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.Permissao;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPermissaoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin/permissoes")
@RequiredArgsConstructor
public class PermissaoAdminController {

    private final GerenciaPermissaoUseCase gerenciaPermissaoUseCase;
    private final PermissaoWebMapper permissaoWebMapper;

    @GetMapping
    public String listarPermissoes(Model model) {
        List<PermissaoResponse> permissoes = gerenciaPermissaoUseCase.listarTodas().stream()
                .map(permissaoWebMapper::toResponse)
                .collect(Collectors.toList());
        
        model.addAttribute("permissoes", permissoes);
        return "admin/permissoes/lista";
    }

    @GetMapping("/nova")
    public String novaPermissao(Model model) {
        model.addAttribute("permissao", new PermissaoRequest());
        return "admin/permissoes/form";
    }

    @GetMapping("/{id}")
    public String editarPermissao(@PathVariable Long id, Model model) {
        Permissao permissao = gerenciaPermissaoUseCase.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Permissão não encontrada com ID: " + id));
        
        model.addAttribute("permissao", permissaoWebMapper.toRequest(permissao));
        return "admin/permissoes/form";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> salvarPermissao(@RequestBody PermissaoRequest request) {
        try {
            log.info("Recebendo requisição para salvar permissão: {}", request);
            
            Map<String, Object> response = new HashMap<>();
            Permissao permissao = permissaoWebMapper.toDomain(request);
            
            if (permissao.getId() != null) {
                // Atualizar permissão existente
                permissao = gerenciaPermissaoUseCase.atualizar(permissao.getId(), permissao);
                response.put("success", true);
                response.put("message", "Permissão atualizada com sucesso");
            } else {
                // Criar nova permissão
                permissao = gerenciaPermissaoUseCase.criar(permissao);
                response.put("success", true);
                response.put("message", "Permissão criada com sucesso");
            }
            
            response.put("id", permissao.getId());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao salvar permissão: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Erro ao salvar permissão", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao salvar permissão: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> excluirPermissao(@PathVariable Long id) {
        try {
            gerenciaPermissaoUseCase.excluir(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Permissão excluída com sucesso");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao excluir permissão: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Erro ao excluir permissão", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao excluir permissão: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/verificar-codigo")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> verificarCodigoExistente(@RequestParam String codigo, @RequestParam(required = false) Long id) {
        boolean exists = gerenciaPermissaoUseCase.existePorCodigo(codigo);
        
        // Se estiver editando uma permissão existente, verificar se o código pertence a ela mesma
        if (exists && id != null) {
            exists = gerenciaPermissaoUseCase.buscarPorCodigo(codigo)
                    .map(permissao -> !permissao.getId().equals(id))
                    .orElse(true);
        }
        
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
