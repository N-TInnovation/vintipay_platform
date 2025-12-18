package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public interface BuscaPagamentoUseCase {
    Page<Pagamento> buscarTodos(Pageable pageable, String reference, StatusPagamentoEnum status);
    Optional<Pagamento> buscarPorReferencia(String reference);
    Page<Pagamento> buscarPorUsuario(Pageable pageable, Long usuarioId, StatusPagamentoEnum status);
    Page<Pagamento> buscarPorUsuarioEAmbiente(Pageable pageable, Long usuarioId, StatusPagamentoEnum status, AmbienteEnum ambiente);
    
    Page<Pagamento> buscarPorUsuarioETenant(Pageable pageable, Long usuarioId, Long tenantId, StatusPagamentoEnum status);
    
    /**
     * Busca pagamentos agrupados por período e status para geração de gráficos no dashboard
     * @param usuarioId ID do usuário 
     * @param dataInicio Data de início do período
     * @param dataFim Data de fim do período
     * @param tipoPeriodo Tipo de período (DIA, MES, ANO)
     * @return Mapa com dados agrupados por status e período
     */
    Map<String, Map<String, Long>> buscarPorPeriodoAgrupado(Long usuarioId, LocalDate dataInicio, LocalDate dataFim, String tipoPeriodo);
    
    Map<String, Map<String, Long>> buscarPorPeriodoAgrupadoComTenant(Long usuarioId, Long tenantId, LocalDate dataInicio, LocalDate dataFim, String tipoPeriodo);
}
