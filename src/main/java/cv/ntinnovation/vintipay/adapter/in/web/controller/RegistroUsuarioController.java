package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.RegistroUsuarioRequest;
import cv.ntinnovation.vintipay.application.port.in.RegistroUsuarioUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/registro")
@RequiredArgsConstructor
public class RegistroUsuarioController {

    private final RegistroUsuarioUseCase registroUsuarioUseCase;

    @GetMapping
    public String exibirFormularioRegistro(Model model) {
        model.addAttribute("registroRequest", new RegistroUsuarioRequest());
        return "registro";
    }

    @PostMapping
    public String registrarUsuario(@Valid @ModelAttribute("registroRequest") RegistroUsuarioRequest request,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "registro";
        }

        try {
            registroUsuarioUseCase.registrarNovoUsuario(
                    request.getUsername(),
                    request.getEmail(),
                    request.getNome(),
                    request.getTelefone(),
                    request.getPassword(),
                    "USER"
            );
            return "redirect:/login?registroSucesso";
        } catch (Exception e) {
            log.error("Erro ao registrar usuário", e);
            model.addAttribute("erro", "Erro ao registrar usuário: " + e.getMessage());
            return "registro";
        }
    }
}
