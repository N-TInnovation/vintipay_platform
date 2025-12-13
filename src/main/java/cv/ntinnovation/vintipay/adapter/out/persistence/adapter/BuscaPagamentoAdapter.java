package cv.ntinnovation.vintipay.adapter.out.persistence.adapter;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.PagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.PagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.port.out.BuscaPagamentoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BuscaPagamentoAdapter implements BuscaPagamentoPort {

    private final PagamentoRepository pagamentoRepository;
    private final PagamentoMapper pagamentoMapper;

    @Override
    public Page<Pagamento> buscarTodos(Pageable pageable, String reference, StatusPagamentoEnum status) {
        log.info("Buscando pagamentos com status: {}", status);
        var pagamentos = reference != null && !reference.isEmpty() ?
                pagamentoRepository.findByReferenceContainingIgnoreCaseAndStatusIfPresent(reference, status, pageable) :
                pagamentoRepository.findByStatusIfPresent(status, pageable);
        return pagamentos.map(pagamentoMapper::toDomain);
    }

    @Override
    public Optional<Pagamento> buscarPorReferencia(String reference) {
        return pagamentoRepository.findByReference(reference)
                .map(pagamentoMapper::toDomain);
    }
    
    @Override
    public Page<Pagamento> buscarPorUsuario(Pageable pageable, Long usuarioId, StatusPagamentoEnum status) {
        // Converter o enum para string apenas se não for nulo
        String statusStr = status != null ? status.name() : null;
        
        try {
            var pagamentos = pagamentoRepository.findByUsuarioIdAndStatusIfPresent(usuarioId, statusStr, pageable);
            return pagamentos.map(pagamentoMapper::toDomain);
        } catch (Exception e) {
            // Em caso de erro na consulta, retornar uma página vazia
            return Page.empty(pageable);
        }
    }
    
    @Override
    public Page<Pagamento> buscarPorUsuarioIdEStatus(Long usuarioId, String status, Pageable pageable) {
        log.info("Buscando pagamentos para usuário ID: {}, status: {}", usuarioId, status);
        try {
            var pagamentos = pagamentoRepository.findByUsuarioIdAndStatusIfPresent(usuarioId, status, pageable);
            log.info("Encontrados {} pagamentos", pagamentos.getTotalElements());
            return pagamentos.map(pagamentoMapper::toDomain);
        } catch (Exception e) {
            log.error("Erro ao buscar pagamentos para usuário ID: {}, status: {}", usuarioId, status, e);
            return Page.empty(pageable);
        }
    }

    @Override
    public Page<Pagamento> buscarPorUsuarioEAmbiente(Pageable pageable, Long usuarioId, StatusPagamentoEnum status, AmbienteEnum ambiente) {
        // Converter o enum para string apenas se não for nulo
        String statusStr = status != null ? status.name() : null;
        // Converter o ambiente para string apenas se não for nulo
        String ambienteStr = ambiente != null ? ambiente.name() : null;
        
        log.info("Buscando pagamentos para usuário ID: {}, status: {}, ambiente: {}", usuarioId, statusStr, ambienteStr);
        try {
            var pagamentos = pagamentoRepository.findByUsuarioIdAndStatusAndAmbienteIfPresent(
                usuarioId, 
                statusStr, 
                ambienteStr, 
                pageable
            );
            log.info("Encontrados {} pagamentos no ambiente {}", pagamentos.getTotalElements(), ambienteStr);
            return pagamentos.map(pagamentoMapper::toDomain);
        } catch (Exception e) {
            log.error("Erro ao buscar pagamentos para usuário ID: {}, status: {}, ambiente: {}", 
                usuarioId, statusStr, ambienteStr, e);
            return Page.empty(pageable);
        }
    }
    
    @Override
    public Map<String, Map<String, Long>> buscarPorPeriodoAgrupado(Long usuarioId, LocalDate dataInicio, LocalDate dataFim, String tipoPeriodo) {
        log.info("Buscando pagamentos agrupados por período para usuário ID: {}, de {} até {}, tipo: {}", 
                usuarioId, dataInicio, dataFim, tipoPeriodo);
        
        try {
            // Buscar todos os pagamentos do usuário no período
            List<PagamentoEntity> pagamentos = pagamentoRepository.findByUsuarioIdAndDataPagamentoBetween(
                usuarioId, 
                dataInicio.atStartOfDay(), 
                dataFim.plusDays(1).atStartOfDay()
            );
            
            log.info("Encontrados {} pagamentos no período", pagamentos.size());
            
            if (pagamentos.isEmpty()) {
                log.warn("Nenhum pagamento encontrado para o período especificado");
                // Se não houver dados, inicializar mapa para cada status
                Map<String, Map<String, Long>> resultadoVazio = new HashMap<>();
                for (StatusPagamentoEnum status : StatusPagamentoEnum.values()) {
                    resultadoVazio.put(status.name(), new HashMap<>());
                }
                return resultadoVazio;
            }
            
            // Verificar se há status nulos
            long countNullStatus = pagamentos.stream().filter(p -> p.getStatus() == null).count();
            if (countNullStatus > 0) {
                log.warn("Detectados {} pagamentos com status nulo", countNullStatus);
            }
            
            // Mapa para armazenar dados agrupados: status -> {período -> quantidade}
            Map<String, Map<String, Long>> resultado = new HashMap<>();
            
            // Inicializar mapa para cada status
            for (StatusPagamentoEnum status : StatusPagamentoEnum.values()) {
                resultado.put(status.name(), new HashMap<>());
            }
            
            // Agrupar pagamentos por status e período
            Map<String, Map<String, List<PagamentoEntity>>> agrupados = pagamentos.stream()
                .filter(p -> p.getStatus() != null) // Filtrar pagamentos com status nulo
                .collect(Collectors.groupingBy(
                    pagamento -> pagamento.getStatus().name(), // Converter enum para String usando name()
                    Collectors.groupingBy(
                        entity -> formatarPeriodo(entity, tipoPeriodo)
                    )
                ));
            
            log.info("Pagamentos agrupados por status: {}", agrupados.keySet());
            
            // Converter para o formato esperado: status -> {período -> quantidade}
            agrupados.forEach((status, periodoMap) -> {
                Map<String, Long> quantidadePorPeriodo = periodoMap.entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (long) entry.getValue().size()
                    ));
                
                log.info("Status {} tem {} períodos com dados", status, quantidadePorPeriodo.size());
                resultado.put(status, quantidadePorPeriodo);
            });
            
            log.info("Resultado final: {}", resultado);
            return resultado;
        } catch (Exception e) {
            log.error("Erro ao buscar pagamentos agrupados por período para usuário ID: {}", usuarioId, e);
            return new HashMap<>();
        }
    }
    
    private String formatarPeriodo(PagamentoEntity entity, String tipoPeriodo) {
        if (entity == null || entity.getCreatedAt() == null) {
            return "SEM DATA";
        }
        
        LocalDate data = entity.getCreatedAt().toLocalDate();
        
        switch (tipoPeriodo.toUpperCase()) {
            case "DIA":
                return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            case "MES":
                return data.format(DateTimeFormatter.ofPattern("MM/yyyy"));
            case "ANO":
                return String.valueOf(data.getYear());
            default:
                return "PERÍODO INVÁLIDO";
        }
    }
}
