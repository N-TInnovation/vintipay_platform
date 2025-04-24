package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.PlanoRequest;
import cv.ntinnovation.vintipay.application.core.domain.entity.Plano;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPlanoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/admin/planos")
@RequiredArgsConstructor
public class PlanoController {

    private final GerenciaPlanoUseCase gerenciaPlanoUseCase;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listarPlanos(Model model) {
        List<Plano> planos = gerenciaPlanoUseCase.listarPlanosAtivos();
        model.addAttribute("planos", planos);
        return "admin/planos/lista";
    }

    @GetMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String exibirFormularioNovoPlano(Model model) {
        model.addAttribute("planoRequest", new PlanoRequest());
        model.addAttribute("periodosCobranca", Plano.PeriodoCobranca.values());
        return "admin/planos/form";
    }

    @PostMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String criarPlano(@Valid @ModelAttribute("planoRequest") PlanoRequest request,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("periodosCobranca", Plano.PeriodoCobranca.values());
            return "admin/planos/form";
        }

        try {
            gerenciaPlanoUseCase.criarPlano(
                    request.getNome(),
                    request.getDescricao(),
                    request.getPreco(),
                    request.getPeriodoCobranca(),
                    request.getDiasTrial(),
                    request.getStripePriceId(),
                    request.isAtivo()
            );
            return "redirect:/admin/planos?sucesso=Plano+criado+com+sucesso";
        } catch (IllegalArgumentException e) {
            // Erro de validação (como nome duplicado)
            log.warn("Erro de validação ao criar plano: {}", e.getMessage());
            model.addAttribute("mensagemErro", e.getMessage());
            model.addAttribute("periodosCobranca", Plano.PeriodoCobranca.values());
            return "admin/planos/form";
        } catch (Exception e) {
            log.error("Erro ao criar plano", e);
            model.addAttribute("mensagemErro", "Erro ao criar plano: " + e.getMessage());
            model.addAttribute("periodosCobranca", Plano.PeriodoCobranca.values());
            return "admin/planos/form";
        }
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String exibirFormularioEditarPlano(@PathVariable Long id, Model model) {
        try {
            log.info("Exibindo formulário de edição para o plano ID: {}", id);
            Optional<Plano> planoOptional = gerenciaPlanoUseCase.buscarPlanoPorId(id);
            if (planoOptional.isEmpty()) {
                log.warn("Plano não encontrado com ID: {}", id);
                return "redirect:/admin/planos?erro=Plano+não+encontrado";
            }

            Plano plano = planoOptional.get();
            log.info("Plano encontrado: {}", plano);
            
            PlanoRequest request = new PlanoRequest();
            request.setNome(plano.getNome());
            request.setDescricao(plano.getDescricao());
            request.setPreco(plano.getPreco());
            request.setPeriodoCobranca(plano.getPeriodoCobranca());
            request.setDiasTrial(plano.getDiasTrial());
            request.setStripePriceId(plano.getStripePriceId());
            request.setAtivo(plano.isAtivo());

            model.addAttribute("planoRequest", request);
            model.addAttribute("planoId", id);
            model.addAttribute("periodosCobranca", Plano.PeriodoCobranca.values());
            
            log.info("Formulário de edição preparado com sucesso para o plano ID: {}", id);
            return "admin/planos/form";
        } catch (Exception e) {
            log.error("Erro ao exibir formulário de edição para o plano ID: {}", id, e);
            return "redirect:/admin/planos?erro=" + e.getMessage();
        }
    }

    @PostMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String atualizarPlano(@PathVariable Long id,
                                @Valid @ModelAttribute("planoRequest") PlanoRequest request,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("planoId", id);
            model.addAttribute("periodosCobranca", Plano.PeriodoCobranca.values());
            return "admin/planos/form";
        }

        try {
            gerenciaPlanoUseCase.atualizarPlano(
                    id,
                    request.getNome(),
                    request.getDescricao(),
                    request.getPreco(),
                    request.getPeriodoCobranca(),
                    request.getDiasTrial(),
                    request.getStripePriceId(),
                    request.isAtivo()
            );
            model.addAttribute("mensagemSucesso", "Plano atualizado com sucesso!");
            model.addAttribute("planoId", id);
            model.addAttribute("periodosCobranca", Plano.PeriodoCobranca.values());
            return "admin/planos/form";
        } catch (IllegalArgumentException e) {
            // Erro de validação (como nome duplicado)
            log.warn("Erro de validação ao atualizar plano: {}", e.getMessage());
            model.addAttribute("mensagemErro", e.getMessage());
            model.addAttribute("planoId", id);
            model.addAttribute("periodosCobranca", Plano.PeriodoCobranca.values());
            return "admin/planos/form";
        } catch (Exception e) {
            log.error("Erro ao atualizar plano", e);
            model.addAttribute("mensagemErro", "Erro ao atualizar plano: " + e.getMessage());
            model.addAttribute("planoId", id);
            model.addAttribute("periodosCobranca", Plano.PeriodoCobranca.values());
            return "admin/planos/form";
        }
    }

    @GetMapping("/ativar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String ativarPlano(@PathVariable Long id) {
        try {
            Optional<Plano> planoOptional = gerenciaPlanoUseCase.buscarPlanoPorId(id);
            if (planoOptional.isEmpty()) {
                return "redirect:/admin/planos?erro=Plano+não+encontrado";
            }

            Plano plano = planoOptional.get();
            gerenciaPlanoUseCase.atualizarPlano(
                    id,
                    plano.getNome(),
                    plano.getDescricao(),
                    plano.getPreco(),
                    plano.getPeriodoCobranca(),
                    plano.getDiasTrial(),
                    plano.getStripePriceId(),
                    true
            );
            return "redirect:/admin/planos?sucesso=Plano+ativado+com+sucesso";
        } catch (Exception e) {
            log.error("Erro ao ativar plano", e);
            return "redirect:/admin/planos?erro=" + e.getMessage();
        }
    }

    @GetMapping("/desativar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String desativarPlano(@PathVariable Long id) {
        try {
            Optional<Plano> planoOptional = gerenciaPlanoUseCase.buscarPlanoPorId(id);
            if (planoOptional.isEmpty()) {
                return "redirect:/admin/planos?erro=Plano+não+encontrado";
            }

            Plano plano = planoOptional.get();
            gerenciaPlanoUseCase.atualizarPlano(
                    id,
                    plano.getNome(),
                    plano.getDescricao(),
                    plano.getPreco(),
                    plano.getPeriodoCobranca(),
                    plano.getDiasTrial(),
                    plano.getStripePriceId(),
                    false
            );
            return "redirect:/admin/planos?sucesso=Plano+desativado+com+sucesso";
        } catch (Exception e) {
            log.error("Erro ao desativar plano", e);
            return "redirect:/admin/planos?erro=" + e.getMessage();
        }
    }
}
