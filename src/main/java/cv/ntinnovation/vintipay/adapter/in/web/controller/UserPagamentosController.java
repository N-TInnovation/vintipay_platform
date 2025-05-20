package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.port.in.BuscaPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user/pagamentos")
@RequiredArgsConstructor
public class UserPagamentosController {

    private final BuscaPagamentoUseCase buscaPagamentoUseCase;
    private static final int PAGE_SIZE = 10;

    @GetMapping({"", "/"})
    public String listarPagamentos(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "status", required = false) StatusPagamentoEnum status,
            @RequestParam(name = "ambiente", required = false, defaultValue = "PRODUCAO") AmbienteEnum ambiente,
            Model model) {
        
        // Obter o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
        
        // Configurar paginação com ordenação por data de criação (mais recentes primeiro)
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("created_at").descending());
        
        try {
            // Buscar pagamentos do usuário com o status e ambiente especificados
            var pagamentos = buscaPagamentoUseCase.buscarPorUsuarioEAmbiente(pageable, usuarioAtual.getId(), status, ambiente);
            
            // Adicionar dados ao modelo
            model.addAttribute("pagamentos", pagamentos);
            model.addAttribute("statusAtual", status);
            model.addAttribute("ambienteAtual", ambiente);
            model.addAttribute("statusOptions", StatusPagamentoEnum.values());
            model.addAttribute("ambienteOptions", AmbienteEnum.values());
            model.addAttribute("paginaAtual", page);
            
            // Adicionar estatísticas
            var pagamentosPagos = buscaPagamentoUseCase.buscarPorUsuarioEAmbiente(
                    PageRequest.of(0, Integer.MAX_VALUE), 
                    usuarioAtual.getId(), 
                    StatusPagamentoEnum.PAGO,
                    ambiente
            ).getTotalElements();
            
            var pagamentosPendentes = buscaPagamentoUseCase.buscarPorUsuarioEAmbiente(
                    PageRequest.of(0, Integer.MAX_VALUE), 
                    usuarioAtual.getId(), 
                    StatusPagamentoEnum.PENDENTE,
                    ambiente
            ).getTotalElements();
            
            var pagamentosCancelados = buscaPagamentoUseCase.buscarPorUsuarioEAmbiente(
                    PageRequest.of(0, Integer.MAX_VALUE), 
                    usuarioAtual.getId(), 
                    StatusPagamentoEnum.CANCELADO,
                    ambiente
            ).getTotalElements();
            
            model.addAttribute("pagamentosPagos", pagamentosPagos);
            model.addAttribute("pagamentosPendentes", pagamentosPendentes);
            model.addAttribute("pagamentosCancelados", pagamentosCancelados);
            
        } catch (Exception e) {
            // Em caso de erro, adicionar mensagem ao modelo
            model.addAttribute("erro", "Ocorreu um erro ao buscar os pagamentos: " + e.getMessage());
        }
        
        return "user/pagamentos";
    }
    
    @GetMapping("/{reference}")
    public String detalhesPagamento(
            @PathVariable String reference,
            Model model) {
        
        // Obter o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
        
        try {
            // Buscar o pagamento pela referência
            var pagamentoOptional = buscaPagamentoUseCase.buscarPorReferencia(reference);
            
            if (pagamentoOptional.isPresent()) {
                var pagamento = pagamentoOptional.get();
                
                // Verificar se o pagamento pertence ao usuário logado
                if (!pagamento.getIntencaoPagamento().getContaPagamento().getEmail()
                        .equals(usuarioAtual.getEmail())) {
                    return "redirect:/user/pagamentos?erro=Acesso+negado";
                }
                
                // Adicionar o pagamento ao modelo
                model.addAttribute("pagamento", pagamento);
                
                return "user/detalhes"; 
            } else {
                return "redirect:/user/pagamentos?erro=Pagamento+não+encontrado";
            }
        } catch (Exception e) {
            // Em caso de erro, redirecionar para a listagem com mensagem de erro
            return "redirect:/user/pagamentos?erro=" + e.getMessage();
        }
    }
}
