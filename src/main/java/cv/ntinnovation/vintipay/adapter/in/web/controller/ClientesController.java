package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.application.core.service.AmbienteService;
import cv.ntinnovation.vintipay.application.port.in.BuscaContaPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller para gerenciar a listagem e detalhes das contas de pagamento (clientes)
 */
@Slf4j
@Controller
@RequestMapping("/user/clientes")
@RequiredArgsConstructor
public class ClientesController {

    private final BuscaContaPagamentoUseCase buscaContaPagamentoUseCase;
    private final AmbienteService ambienteService;
    
    private static final int PAGE_SIZE = 10;

    /**
     * Método para listar todas as contas de pagamento (clientes) com paginação
     */
    @GetMapping({"", "/"})
    public String listarClientes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "query", required = false) String query,
            HttpServletRequest request,
            Model model) {
        
        // Configurar paginação com ordenação por nome (A-Z)
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("name").ascending());
        
        try {
            // Buscar todas as contas de pagamento ou filtrar por nome/email se houver query
            var clientes = query != null && !query.trim().isEmpty()
                    ? buscaContaPagamentoUseCase.buscarPorNomeOuEmail(query, pageable)
                    : buscaContaPagamentoUseCase.buscarTodos(pageable);
            
            if (query != null && !query.trim().isEmpty()) {
                model.addAttribute("query", query);
            }
            
            // Adicionar dados ao modelo
            model.addAttribute("clientes", clientes);
            model.addAttribute("paginaAtual", page);
            
            // Adicionar estatísticas
            model.addAttribute("totalClientes", clientes.getTotalElements());
            
        } catch (Exception e) {
            // Em caso de erro, adicionar mensagem ao modelo
            model.addAttribute("erro", "Ocorreu um erro ao buscar os clientes: " + e.getMessage());
            log.error("Erro ao buscar clientes", e);
        }
        
        return "user/clientes";
    }
    
    /**
     * Método para exibir detalhes de um cliente específico
     */
    @GetMapping("/{id}")
    public String detalhesCliente(
            @PathVariable Long id,
            HttpServletRequest request,
            Model model) {
        
        try {
            // Buscar a conta de pagamento pelo ID
            var clienteOptional = buscaContaPagamentoUseCase.buscarPorId(id);
            
            if (clienteOptional.isPresent()) {
                var cliente = clienteOptional.get();
                model.addAttribute("cliente", cliente);
                
                // TODO: Buscar estatísticas de pagamentos para este cliente
                
                return "user/cliente-detalhes";
            } else {
                return "redirect:/user/clientes?erro=Cliente+não+encontrado";
            }
            
        } catch (Exception e) {
            // Em caso de erro, redirecionar para a listagem com mensagem de erro
            log.error("Erro ao buscar detalhes do cliente ID: " + id, e);
            return "redirect:/user/clientes?erro=" + e.getMessage();
        }
    }
}
