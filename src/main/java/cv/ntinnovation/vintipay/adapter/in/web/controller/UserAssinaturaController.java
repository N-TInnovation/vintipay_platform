package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.out.db.repository.PagamentoRepository;
import cv.ntinnovation.vintipay.adapter.out.db.entity.PagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.PlanoAssinaturaEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.PlanoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.PagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.PlanoAssinaturaMapper;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.PlanoMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.PlanoAssinaturaRepository;
import cv.ntinnovation.vintipay.adapter.out.db.repository.PlanoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.core.domain.entity.Plano;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaAssinaturaUseCase;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPlanoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/assinatura")
@Log
public class UserAssinaturaController {

    private final PagamentoRepository pagamentoRepository;
    private final PagamentoMapper pagamentoMapper;
    private final GerenciaAssinaturaUseCase gerenciaAssinaturaUseCase;
    private final GerenciaPlanoUseCase gerenciaPlanoUseCase;
    private final PlanoAssinaturaRepository planoAssinaturaRepository;
    private final PlanoRepository planoRepository;
    private final PlanoAssinaturaMapper planoAssinaturaMapper;
    private final PlanoMapper planoMapper;
    
    @Value("${app.base-url:https://localhost:8443}")
    private String baseUrl;

    @Autowired
    public UserAssinaturaController(
            PagamentoRepository pagamentoRepository,
            PagamentoMapper pagamentoMapper,
            GerenciaAssinaturaUseCase gerenciaAssinaturaUseCase,
            GerenciaPlanoUseCase gerenciaPlanoUseCase,
            PlanoAssinaturaRepository planoAssinaturaRepository,
            PlanoRepository planoRepository,
            PlanoAssinaturaMapper planoAssinaturaMapper,
            PlanoMapper planoMapper) {
        this.pagamentoRepository = pagamentoRepository;
        this.pagamentoMapper = pagamentoMapper;
        this.gerenciaAssinaturaUseCase = gerenciaAssinaturaUseCase;
        this.gerenciaPlanoUseCase = gerenciaPlanoUseCase;
        this.planoAssinaturaRepository = planoAssinaturaRepository;
        this.planoRepository = planoRepository;
        this.planoAssinaturaMapper = planoAssinaturaMapper;
        this.planoMapper = planoMapper;
        log.info("UserAssinaturaController inicializado com sucesso");
    }

    @GetMapping({"", "/"})
    public String assinaturaPage(Model model, 
                                @RequestParam(required = false, defaultValue = "0") int page,
                                @RequestParam(required = false, defaultValue = "10") int size) {
        try {
            log.info("Iniciando carregamento da página de assinatura");
            
            // Obter o usuário autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
            log.info("Usuário autenticado: " + usuarioAtual.getEmail());
            
            // Adicionar dados do usuário ao modelo
            model.addAttribute("usuario", usuarioAtual);
            
            // Buscar assinatura do usuário (primeiro pelo repositório para garantir dados mais recentes)
            Optional<PlanoAssinaturaEntity> assinaturaEntityOptional = planoAssinaturaRepository.findByUsuarioId(usuarioAtual.getId());
            
            if (assinaturaEntityOptional.isPresent()) {
                log.info("Assinatura encontrada no repositório");
                PlanoAssinaturaEntity assinaturaEntity = assinaturaEntityOptional.get();
                
                // Converter a entidade para o objeto de domínio
                PlanoAssinatura assinatura = planoAssinaturaMapper.toDomain(assinaturaEntity);
                
                // Buscar o plano associado
                Optional<PlanoEntity> planoEntityOptional = planoRepository.findById(assinaturaEntity.getPlanoId());
                
                if (planoEntityOptional.isPresent()) {
                    Plano plano = planoMapper.toDomain(planoEntityOptional.get());
                    
                    // Formatar datas
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String dataInicio = assinatura.getDataInicio().format(formatter);
                    String dataRenovacao = assinatura.getProximoPagamento() != null ? 
                                          assinatura.getProximoPagamento().format(formatter) : 
                                          "Não disponível";
                    
                    // Adicionar informações da assinatura ao modelo
                    model.addAttribute("planoAtual", plano.getNome());
                    model.addAttribute("dataInicio", dataInicio);
                    model.addAttribute("dataRenovacao", dataRenovacao);
                    model.addAttribute("statusAssinatura", assinatura.getStatus().toString());
                    model.addAttribute("valorMensal", "R$ " + plano.getPreco());
                    model.addAttribute("assinaturaId", assinatura.getId());
                    model.addAttribute("planoId", plano.getId());
                    
                    // Adicionar recursos do plano
                    String[] recursos = plano.getDescricao().split(",");
                    model.addAttribute("recursos", recursos);
                    
                    log.info("Dados da assinatura adicionados ao modelo: " + plano.getNome() + ", Status: " + assinatura.getStatus());
                } else {
                    log.warning("Plano não encontrado para a assinatura ID: " + assinaturaEntity.getId());
                    setDefaultSubscriptionData(model);
                }
            } else {
                // Tentar buscar pelo use case como fallback
                log.info("Assinatura não encontrada no repositório, tentando pelo use case");
                Optional<PlanoAssinatura> assinaturaOptional = gerenciaAssinaturaUseCase.buscarAssinaturaPorUsuario(usuarioAtual);
                
                if (assinaturaOptional.isPresent()) {
                    log.info("Assinatura encontrada pelo use case");
                    PlanoAssinatura assinatura = assinaturaOptional.get();
                    Plano plano = assinatura.getPlano();
                    
                    // Formatar datas
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String dataInicio = assinatura.getDataInicio().format(formatter);
                    String dataRenovacao = assinatura.getProximoPagamento() != null ? 
                                          assinatura.getProximoPagamento().format(formatter) : 
                                          "Não disponível";
                    
                    // Adicionar informações da assinatura ao modelo
                    model.addAttribute("planoAtual", plano.getNome());
                    model.addAttribute("dataInicio", dataInicio);
                    model.addAttribute("dataRenovacao", dataRenovacao);
                    model.addAttribute("statusAssinatura", assinatura.getStatus().toString());
                    model.addAttribute("valorMensal", "R$ " + plano.getPreco());
                    model.addAttribute("assinaturaId", assinatura.getId());
                    model.addAttribute("planoId", plano.getId());
                    
                    // Adicionar recursos do plano
                    String[] recursos = plano.getDescricao().split(",");
                    model.addAttribute("recursos", recursos);
                    
                    log.info("Dados da assinatura adicionados ao modelo: " + plano.getNome() + ", Status: " + assinatura.getStatus());
                } else {
                    log.info("Usuário não possui assinatura");
                    setDefaultSubscriptionData(model);
                }
            }
            
            // Buscar planos disponíveis para upgrade
            List<Plano> planosDisponiveis;
            try {
                planosDisponiveis = gerenciaPlanoUseCase.listarPlanosAtivos();
                log.info("Planos disponíveis carregados: " + planosDisponiveis.size());
            } catch (Exception e) {
                log.log(Level.WARNING, "Erro ao buscar planos disponíveis: " + e.getMessage(), e);
                planosDisponiveis = new ArrayList<>();
            }
            
            model.addAttribute("planosDisponiveis", planosDisponiveis);
            
            // Buscar histórico de pagamentos
            try {
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
                Page<PagamentoEntity> pagamentosPage = pagamentoRepository.findByUsuarioIdAndStatusIfPresent(
                        usuarioAtual.getId(), null, pageable);
                
                List<Pagamento> pagamentos = pagamentosPage.getContent().stream()
                        .map(pagamentoMapper::toDomain)
                        .collect(Collectors.toList());
                
                log.info("Histórico de pagamentos carregado: " + pagamentos.size() + " registros");
                
                model.addAttribute("pagamentos", pagamentos);
                model.addAttribute("paginaAtual", pagamentosPage.getNumber());
                model.addAttribute("totalPaginas", pagamentosPage.getTotalPages());
                model.addAttribute("totalItens", pagamentosPage.getTotalElements());
            } catch (Exception e) {
                log.log(Level.SEVERE, "Erro ao buscar histórico de pagamentos: " + e.getMessage(), e);
                model.addAttribute("mensagemErro", "Não foi possível carregar o histórico de pagamentos: " + e.getMessage());
            }
            
            log.info("Página de assinatura carregada com sucesso");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Erro ao carregar página de assinatura: " + e.getMessage(), e);
            model.addAttribute("mensagemErro", "Ocorreu um erro ao carregar a página: " + e.getMessage());
        }
        
        return "user/assinatura";
    }
    
    /**
     * Configura dados padrão para usuários sem assinatura
     */
    private void setDefaultSubscriptionData(Model model) {
        model.addAttribute("planoAtual", "Sem plano");
        model.addAttribute("dataInicio", "N/A");
        model.addAttribute("dataRenovacao", "N/A");
        model.addAttribute("statusAssinatura", "Inativa");
        model.addAttribute("valorMensal", "R$ 0,00");
        model.addAttribute("assinaturaId", null);
        model.addAttribute("planoId", null);
        
        // Recursos padrão
        String[] recursos = {
            "Nenhum recurso disponível",
            "Assine um plano para acessar os recursos"
        };
        model.addAttribute("recursos", recursos);
    }
}
