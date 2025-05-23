package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoIntegracoes;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.domain.enums.IntegracaoEnum;
import cv.ntinnovation.vintipay.application.core.domain.rules.integracao.VendusErp;
import cv.ntinnovation.vintipay.application.port.in.AtualizaIntegracoesPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.BuscaIntegracoesUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/integracoes")
@RequiredArgsConstructor
@Slf4j
public class UserIntegracoesController {

    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoUseCase;
    private final AtualizaIntegracoesPagamentoUseCase atualizaIntegracoesPagamentoUseCase;
    private final BuscaIntegracoesUseCase buscaIntegracoesUseCase;

    @GetMapping({"", "/"})
    public String listarIntegracoes(@AuthenticationPrincipal Usuario usuario, Model model) {
        log.info("Listando integrações para o usuário ID: {}", usuario.getId());
        
        // Buscar integrações diretamente pelo caso de uso específico
        List<ConfiguracaoIntegracoes> integracoes = buscaIntegracoesUseCase.buscarTodasIntegracoesPorIdUsuario(usuario.getId());
        log.info("Total de integrações encontradas: {}", integracoes.size());
        
        model.addAttribute("integracoes", integracoes);
        model.addAttribute("tiposIntegracao", Arrays.asList(IntegracaoEnum.values()));
        
        return "user/integracoes/lista";
    }

    @GetMapping("/novo")
    public String novaIntegracao(@AuthenticationPrincipal Usuario usuario,
                                 @RequestParam(value = "tipoI", required = false) String tipoI, Model model) {
        // Criar nova configuração vazia
        ConfiguracaoIntegracoes novaIntegracao = new ConfiguracaoIntegracoes(
            null, true, IntegracaoEnum.getByName(tipoI), "", "", "", "", "", null, null, null,
        null);

        var integracaoEnumList = Arrays.asList(IntegracaoEnum.values());
        model.addAttribute("integracao", novaIntegracao);
        model.addAttribute("tiposIntegracao", integracaoEnumList.stream().map(IntegracaoEnum::getName).collect(Collectors.toList()));
        
        // Buscar configurações de pagamento do usuário para associação
        List<ConfiguracaoPagamento> configsPagamento = buscaConfiguracaoPagamentoUseCase.buscarPorUsuarioIdLista(usuario.getId());
        
        if (configsPagamento.isEmpty()) {
            // Se não houver configurações, enviar uma mensagem para o usuário
            model.addAttribute("aviso", "Você não possui configurações de pagamento. Crie uma configuração antes de configurar integrações.");
        }
        
        model.addAttribute("configuracoesPagamento", configsPagamento);
        
        return "user/integracoes/form";
    }

    @PostMapping("/salvar")
    public String salvarIntegracao(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) Long id,
            @RequestParam("integracaoEnum") String integracaoTipo,
            @RequestParam String description,
            @RequestParam(required = false) String version,
            @RequestParam(required = false) String baseUrl,
            @RequestParam String apiKey,
            @RequestParam(required = false) String apiSecret,
            @RequestParam Long configuracaoPagamentoId,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Iniciando salvamento de integração: tipo={}, configuracaoId={}, userId={}", 
                integracaoTipo, configuracaoPagamentoId, usuario.getId());
        
        try {
            // Buscar configuração do usuário pelo ID fornecido
            ConfiguracaoPagamento configuracao = buscaConfiguracaoPagamentoUseCase.buscarPorId(configuracaoPagamentoId);
            
            if (configuracao == null) {
                log.error("Configuração de pagamento não encontrada: id={}", configuracaoPagamentoId);
                redirectAttributes.addFlashAttribute("erro", "Configuração de pagamento não encontrada");
                return "redirect:/user/integracoes";
            }
            
            log.info("Configuração de pagamento encontrada: id={}, userId={}", 
                    configuracao.getId(), configuracao.getUsuarioId());
            
            // Verificar se a configuração pertence ao usuário atual
            if (!configuracao.getUsuarioId().equals(usuario.getId())) {
                log.error("Usuário sem permissão: usuarioId={}, configuracaoUsuarioId={}", 
                        usuario.getId(), configuracao.getUsuarioId());
                redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para modificar esta configuração");
                return "redirect:/user/integracoes";
            }

            // Verifica se o integracaoTipo é válido
            IntegracaoEnum integracaoEnum;
            try {
                // Converte para minúsculo para garantir compatibilidade
                if (integracaoTipo != null) {
                    integracaoTipo = integracaoTipo.toLowerCase();
                }
                
                integracaoEnum = IntegracaoEnum.getByName(integracaoTipo);
                if (integracaoEnum == null) {
                    log.error("Tipo de integração inválido: {}", integracaoTipo);
                    redirectAttributes.addFlashAttribute("erro", "Tipo de integração inválido: " + integracaoTipo);
                    return "redirect:/user/integracoes";
                }
            } catch (Exception e) {
                log.error("Erro ao converter tipo de integração: {}", integracaoTipo, e);
                redirectAttributes.addFlashAttribute("erro", "Tipo de integração inválido: " + integracaoTipo);
                return "redirect:/user/integracoes";
            }

            var existeIntegracao = buscaIntegracoesUseCase.existeTipoIntegracaoEmConfiguracaoPagamentoPorUsuario(configuracao.getId(), integracaoTipo, usuario.getId());
            System.out.println(existeIntegracao);
            // Verificar se a configuração já tem essa integração atual
            if (existeIntegracao) {
                log.warn("Já existe integração do mesmo tipo: tipo={}, configuracaoId={}", 
                        integracaoTipo, configuracao.getId());
                redirectAttributes.addFlashAttribute("erro", "Já existe uma integração do "+integracaoTipo+" com esta configuração de pagamento");
                return "redirect:/user/integracoes";
            }
            
            // Aplicar valores padrão conforme tipo de integração
            if (integracaoEnum == IntegracaoEnum.VENDUS) {
                log.info("Configurando integração VENDUS com valores padrão");
                VendusErp vendusErp = new VendusErp();
                baseUrl = vendusErp.getApiUrl();
                version = vendusErp.getVersoesSuportadas().get(0); // Versão mais recente
                apiSecret = ""; // Defina como string vazia em vez de null
            }

            // Se API Secret for null, definir como string vazia para evitar erros
            if (apiSecret == null) {
                apiSecret = "";
            }

            // Verificar se é uma atualização ou uma nova integração
            ConfiguracaoIntegracoes aplicacao;
            LocalDateTime agora = LocalDateTime.now();
            
            if (id != null) {
                log.info("Atualizando integração existente: id={}", id);
                // Atualização - buscar aplicação existente
                try {
                    aplicacao = configuracao.getConfiguracaoAplicacoes().stream()
                            .filter(app -> app.getId().equals(id))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Aplicação não encontrada"));
                    
                    aplicacao.setIntegracaoEnum(integracaoEnum);
                    aplicacao.setDescription(description);
                    aplicacao.setVersion(version);
                    aplicacao.setBaseUrl(baseUrl);
                    aplicacao.setApiKey(apiKey);
                    aplicacao.setApiSecret(apiSecret);
                    aplicacao.setUpdatedAt(agora);
                    aplicacao.setUsuario(usuario);
                    aplicacao.setConfiguracaoPagamento(configuracao);
                } catch (Exception e) {
                    log.error("Erro ao buscar aplicação para atualizar: id={}", id, e);
                    redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar integração: " + e.getMessage());
                    return "redirect:/user/integracoes";
                }
            } else {
                log.info("Criando nova integração: tipo={}, apiKey={}", integracaoEnum, apiKey);
                // Nova aplicação
                aplicacao = ConfiguracaoIntegracoes.builder()
                        .isActive(true)
                        .integracaoEnum(integracaoEnum)
                        .description(description)
                        .version(version != null ? version : "")
                        .baseUrl(baseUrl != null ? baseUrl : "")
                        .apiKey(apiKey)
                        .apiSecret(apiSecret)
                        .createdAt(agora)
                        .updatedAt(agora)
                        .configuracaoPagamento(configuracao)
                        .usuario(usuario)
                        .build();
            }

            // Salvar a configuração atualizada
            log.info("Salvando integração: id={}, tipo={}", 
                    aplicacao.getId() != null ? aplicacao.getId() : "nova", aplicacao.getIntegracaoEnum());
            
            try {
                atualizaIntegracoesPagamentoUseCase.atualizar(aplicacao);
                log.info("Integração salva com sucesso: id={}", aplicacao.getId());
                redirectAttributes.addFlashAttribute("sucesso", "Integração salva com sucesso");
            } catch (Exception e) {
                log.error("Erro ao salvar integração no caso de uso", e);
                redirectAttributes.addFlashAttribute("erro", "Erro ao salvar integração: " + e.getMessage());
            }
            
        } catch (Exception e) {
            log.error("Erro inesperado ao salvar integração", e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar integração: " + e.getMessage());
        }
        
        return "redirect:/user/integracoes";
    }

    @GetMapping("/editar/{id}")
    public String editarIntegracao(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Optional<ConfiguracaoIntegracoes> configuracaoOpt =  buscaIntegracoesUseCase.buscarIntegracaoPorIdUsuario(id, usuario.getId());

        if (configuracaoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Configuração ou aplicação não encontrada");
            return "redirect:/user/integracoes";
        }

        ConfiguracaoIntegracoes integracao = configuracaoOpt.get();


        model.addAttribute("integracao", integracao);
        model.addAttribute("tiposIntegracao", Arrays.asList(IntegracaoEnum.values()).stream().map(IntegracaoEnum::getName).collect(Collectors.toList()));
        
        // Buscar configurações de pagamento do usuário para associação
        List<ConfiguracaoPagamento> configsPagamento = buscaConfiguracaoPagamentoUseCase.buscarPorUsuarioIdLista(usuario.getId());
        
        model.addAttribute("configuracoesPagamento", configsPagamento);
        
        return "user/integracoes/form";
    }

    @GetMapping("/alternar-status/{id}")
    public String alternarStatus(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            System.out.println(usuario.getId());
            Optional<ConfiguracaoIntegracoes> configuracaoOpt = buscaIntegracoesUseCase.buscarIntegracaoPorIdUsuario(id, usuario.getId());

            if (configuracaoOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Configuração de Integração não encontrada");
                return "redirect:/user/integracoes";
            }


            ConfiguracaoIntegracoes aplicacao = configuracaoOpt.get();
            aplicacao.setActive(!aplicacao.isActive());
            aplicacao.setUpdatedAt(LocalDateTime.now());

            atualizaIntegracoesPagamentoUseCase.atualizar(aplicacao);
            redirectAttributes.addFlashAttribute("sucesso", "Status da integração alterado com sucesso");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao alterar status da integração: " + e.getMessage());
        }

        return "redirect:/user/integracoes";
    }

    @GetMapping("/excluir/{id}")
    public String excluirIntegracao(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Optional<ConfiguracaoIntegracoes> configuracaoOpt = buscaIntegracoesUseCase.buscarIntegracaoPorIdUsuario(id, usuario.getId());

            if (configuracaoOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Configuração de Integração não encontrada");
                return "redirect:/user/integracoes";
            }

            var removido = atualizaIntegracoesPagamentoUseCase.deletarPorIdUsuario(id, usuario.getId());

            if (!removido) {
                redirectAttributes.addFlashAttribute("erro", "Aplicação não encontrada");
                return "redirect:/user/integracoes";
            }

            redirectAttributes.addFlashAttribute("sucesso", "Integração excluída com sucesso");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir integração: " + e.getMessage());
        }

        return "redirect:/user/integracoes";
    }
}
