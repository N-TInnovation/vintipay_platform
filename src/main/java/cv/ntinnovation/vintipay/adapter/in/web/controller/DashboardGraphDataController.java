package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.GraficoPagamentoDTO;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.BuscaPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardGraphDataController {

    private final BuscaPagamentoUseCase buscaPagamentoUseCase;
    
    @GetMapping("/pagamentos-por-periodo")
    public ResponseEntity<GraficoPagamentoDTO> getPagamentosPorPeriodo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(defaultValue = "MES") String tipoPeriodo) {
            
        log.info("Recebida requisição para gráficos de pagamentos: dataInicio={}, dataFim={}, tipoPeriodo={}", 
                dataInicio, dataFim, tipoPeriodo);
            
        // Obter o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
        log.info("Usuário autenticado: id={}, nome={}", usuarioAtual.getId(), usuarioAtual.getNome());
        
        // Definir período padrão se não informado (últimos 6 meses)
        if (dataFim == null) {
            dataFim = LocalDate.now();
        }
        
        if (dataInicio == null) {
            dataInicio = dataFim.minusMonths(6);
        }
        
        log.info("Período final para consulta: de {} até {}", dataInicio, dataFim);
        
        try {
            // Buscar dados do banco de dados
            Map<String, Map<String, Long>> dadosPagamentos = 
                buscaPagamentoUseCase.buscarPorPeriodoAgrupado(
                    usuarioAtual.getId(), 
                    dataInicio, 
                    dataFim, 
                    tipoPeriodo
                );
            
            log.info("Dados recuperados: {}", dadosPagamentos);
            
            // Gerar rótulos para o gráfico (ex: nomes dos meses)
            List<String> labels = gerarLabels(dataInicio, dataFim, tipoPeriodo);
            log.info("Labels gerados: {}", labels);
            
            // Converter dados para o formato esperado pelo Chart.js
            GraficoPagamentoDTO graficoDTO = new GraficoPagamentoDTO();
            graficoDTO.setLabels(labels);
            graficoDTO.setDados(dadosPagamentos);
            
            return ResponseEntity.ok(graficoDTO);
        } catch (Exception e) {
            log.error("Erro ao processar dados para gráficos", e);
            
            // Retornar DTO vazio para não quebrar o frontend
            GraficoPagamentoDTO graficoVazio = new GraficoPagamentoDTO();
            graficoVazio.setLabels(new ArrayList<>());
            graficoVazio.setDados(Map.of());
            
            return ResponseEntity.ok(graficoVazio);
        }
    }
    
    private List<String> gerarLabels(LocalDate dataInicio, LocalDate dataFim, String tipoPeriodo) {
        List<String> labels = new ArrayList<>();
        
        switch (tipoPeriodo.toUpperCase()) {
            case "MES":
                // Gerar rótulos para meses (ex: Jan/2023, Fev/2023, etc.)
                YearMonth inicio = YearMonth.from(dataInicio);
                YearMonth fim = YearMonth.from(dataFim);
                long numMeses = ChronoUnit.MONTHS.between(inicio, fim) + 1;
                
                for (int i = 0; i < numMeses; i++) {
                    YearMonth mes = inicio.plusMonths(i);
                    labels.add(formatarMes(mes));
                }
                break;
                
            case "DIA":
                // Gerar rótulos para dias (ex: 01/01/2023, 02/01/2023, etc.)
                long numDias = ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
                for (int i = 0; i < numDias; i++) {
                    LocalDate dia = dataInicio.plusDays(i);
                    labels.add(formatarDia(dia));
                }
                break;
                
            case "ANO":
                // Gerar rótulos para anos (ex: 2022, 2023, etc.)
                int anoInicio = dataInicio.getYear();
                int anoFim = dataFim.getYear();
                for (int ano = anoInicio; ano <= anoFim; ano++) {
                    labels.add(String.valueOf(ano));
                }
                break;
                
            default:
                labels.add("Período Inválido");
        }
        
        return labels;
    }
    
    private String formatarMes(YearMonth mes) {
        String[] mesesAbreviados = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", 
                                    "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        return mesesAbreviados[mes.getMonthValue() - 1] + "/" + mes.getYear();
    }
    
    private String formatarDia(LocalDate dia) {
        return String.format("%02d/%02d/%d", dia.getDayOfMonth(), dia.getMonthValue(), dia.getYear());
    }
}
