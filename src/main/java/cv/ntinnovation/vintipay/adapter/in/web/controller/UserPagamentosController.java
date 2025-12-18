package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.config.EnvironmentProperties;
import cv.ntinnovation.vintipay.adapter.in.web.dto.SispCheckoutRequest;
import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.IntencaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.MoedaEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.exception.PagamentoNaoEncontradoException;
import cv.ntinnovation.vintipay.application.core.service.AmbienteService;
import cv.ntinnovation.vintipay.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.BuscaIntencaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.BuscaPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.EstornarPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
@RequestMapping("/user/pagamentos")
@RequiredArgsConstructor
public class UserPagamentosController {

    private final BuscaPagamentoUseCase buscaPagamentoUseCase;
    private final EstornarPagamentoUseCase estornarPagamentoUseCase;
    private static final int PAGE_SIZE = 10;

    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoUseCase;
    private final EnvironmentProperties environmentProperties;
    private final AmbienteService ambienteService;

    @Value("${sisp.estorno.return.url}")
    private String returnUrl;

    @GetMapping({"", "/"})
    public String listarPagamentos(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "status", required = false) StatusPagamentoEnum status,
            @RequestParam(name = "ambiente", required = false) AmbienteEnum ambiente,
            HttpServletRequest request,
            Model model) {
        
        // Resolver o ambiente a ser usado (parâmetro ou sessão)
        AmbienteEnum ambienteResolvido = ambienteService.resolveAmbiente(request, ambiente);
        
        // Obter o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        Usuario usuarioAtual = customOAuth2User.getUsuario();
        
        // Configurar paginação com ordenação por data de criação (mais recentes primeiro)
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("created_at").descending());
        
        try {
            // Buscar pagamentos do usuário com o status e ambiente especificados
            var pagamentos = buscaPagamentoUseCase.buscarPorUsuarioEAmbiente(pageable, usuarioAtual.getId(), status, ambienteResolvido);
            
            // Adicionar dados ao modelo
            model.addAttribute("pagamentos", pagamentos);
            model.addAttribute("statusAtual", status);
            model.addAttribute("ambienteAtual", ambienteResolvido);
            model.addAttribute("statusOptions", StatusPagamentoEnum.values());
            model.addAttribute("ambienteOptions", AmbienteEnum.values());
            model.addAttribute("paginaAtual", page);
            
            // Adicionar estatísticas
            var pagamentosPagos = buscaPagamentoUseCase.buscarPorUsuarioEAmbiente(
                    PageRequest.of(0, Integer.MAX_VALUE), 
                    usuarioAtual.getId(), 
                    StatusPagamentoEnum.PAGO,
                    ambienteResolvido
            ).getTotalElements();
            
            var pagamentosPendentes = buscaPagamentoUseCase.buscarPorUsuarioEAmbiente(
                    PageRequest.of(0, Integer.MAX_VALUE), 
                    usuarioAtual.getId(), 
                    StatusPagamentoEnum.PENDENTE,
                    ambienteResolvido
            ).getTotalElements();
            
            var pagamentosCancelados = buscaPagamentoUseCase.buscarPorUsuarioEAmbiente(
                    PageRequest.of(0, Integer.MAX_VALUE), 
                    usuarioAtual.getId(), 
                    StatusPagamentoEnum.CANCELADO,
                    ambienteResolvido
            ).getTotalElements();

            var pagamentosEstornados = buscaPagamentoUseCase.buscarPorUsuarioEAmbiente(
                    PageRequest.of(0, Integer.MAX_VALUE),
                    usuarioAtual.getId(),
                    StatusPagamentoEnum.ESTORNADO,
                    ambienteResolvido
            ).getTotalElements();
            
            model.addAttribute("pagamentosPagos", pagamentosPagos);
            model.addAttribute("pagamentosPendentes", pagamentosPendentes);
            model.addAttribute("pagamentosCancelados", pagamentosCancelados);
            model.addAttribute("pagamentosEstornados", pagamentosEstornados);
            
        } catch (Exception e) {
            // Em caso de erro, adicionar mensagem ao modelo
            model.addAttribute("erro", "Ocorreu um erro ao buscar os pagamentos: " + e.getMessage());
        }
        
        return "user/pagamentos";
    }
    
    @GetMapping("/{reference}")
    public String detalhesPagamento(
            @PathVariable String reference,
            Model model, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Usuario usuarioAtual = customOAuth2User.getUsuario();
        
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

                log.info("Pagamento encontrado: {}", pagamento);
                
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

    @GetMapping("/{reference}/estornar")
    public String estornarPagamento(@PathVariable String reference, Model model, @CookieValue(value = "sessao", required = false) String sessionId) {

        var pagamento = estornarPagamentoUseCase.estornar(reference);

        var referenceNumber = pagamento.getIntencaoPagamento().getReferenceNumber() == null ? null : pagamento.getIntencaoPagamento().getReferenceNumber().toString();
        var entityCode = pagamento.getIntencaoPagamento().getEntityCode() == null ? null : pagamento.getIntencaoPagamento().getEntityCode();
        var postId = pagamento.getIntencaoPagamento().getPosId();
        var configuracaoPagamento = buscaConfiguracaoPagamentoUseCase.buscarPorPosId(postId);

        if(configuracaoPagamento.getAtivo() == null || !configuracaoPagamento.getAtivo()) {
            throw new PagamentoNaoEncontradoException("Configuração de pagamento inativa");
        }

        var submitFormUrl = configuracaoPagamento.getAmbiente().getSubmitNormalUrl(environmentProperties);

        var requestResponse = SispCheckoutRequest.builder()
                .timestamp(pagamento.getTimestamp())
                .purchaseRequest(null)
                .fingerprint(pagamento.getFingerprint())
                .fingerprintversion(null) // Versão fixa por enquanto
                .amount(pagamento.getIntencaoPagamento().getValor().toString())
                .transactionCode(IntencaoPagamentoEnum.ESTORNO.getTransactionCode())
                .posID(pagamento.getIntencaoPagamento().getPosId())
                .merchantRef(pagamento.getIntencaoPagamento().getMerchantRef())
                .merchantSession(pagamento.getIntencaoPagamento().getMerchantSession())
                .currency(MoedaEnum.getByName(pagamento.getIntencaoPagamento().getMoeda()).getCodigoISO())
                .is3DSec(null) // Habilitado por padrão
                .urlMerchantResponse(String.format("%s/%s?fingerPrintCode=%s", returnUrl, pagamento.getIntencaoPagamento().getReferencia(), URLEncoder.encode(pagamento.getAuthorizationCode())))
                .languageMessages("pt") // Português por padrão
                .entityCode(entityCode)
                .referenceNumber(referenceNumber)
                .clearingPeriod(pagamento.getClearingPeriod())
                .transactionID(pagamento.getTransactionId())
                .reversal("R")
                .submitFormUrl(String.format("%s?FingerPrint=%s&TimeStamp=%s&FingerPrintVersion=%s",
                        submitFormUrl,
                        UriUtils.encode(pagamento.getFingerprint(), StandardCharsets.UTF_8),
                        UriUtils.encode(pagamento.getTimestamp(), StandardCharsets.UTF_8),
                        UriUtils.encode("1", StandardCharsets.UTF_8)))
                .build();

        model.addAttribute("pagamento", pagamento);
        model.addAttribute("request", requestResponse);
        model.addAttribute("sessao", sessionId);

        return "estorno";
    }
}
