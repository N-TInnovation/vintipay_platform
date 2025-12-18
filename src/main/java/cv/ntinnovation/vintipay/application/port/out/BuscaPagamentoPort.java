package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public interface BuscaPagamentoPort {
    Page<Pagamento> buscarTodos(Pageable pageable, String reference, StatusPagamentoEnum status);
    Optional<Pagamento> buscarPorReferencia(String reference);
    Page<Pagamento> buscarPorUsuario(Pageable pageable, Long usuarioId, StatusPagamentoEnum status);
    
    /**
     * Busca pagamentos por usuário e status (string)
     * @param usuarioId ID do usuário
     * @param status Status do pagamento (opcional, pode ser null)
     * @param pageable Informações de paginação
     * @return Página com os pagamentos do usuário
     */
    Page<Pagamento> buscarPorUsuarioIdEStatus(Long usuarioId, String status, Pageable pageable);
    
    /**
     * Busca pagamentos por usuário, status e ambiente
     * @param pageable Informações de paginação
     * @param usuarioId ID do usuário
     * @param status Status do pagamento (opcional, pode ser null)
     * @param ambiente Ambiente do pagamento (opcional, pode ser null para todos os ambientes)
     * @return Página com os pagamentos do usuário
     */
    Page<Pagamento> buscarPorUsuarioEAmbiente(Pageable pageable, Long usuarioId, StatusPagamentoEnum status, AmbienteEnum ambiente);
    
    /**
     * Busca pagamentos por usuário, tenant e status
     * @param pageable Informações de paginação
     * @param usuarioId ID do usuário
     * @param tenantId ID do tenant
     * @param status Status do pagamento (opcional, pode ser null)
     * @return Página com os pagamentos do usuário filtrados por tenant
     */
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
    
    /**
     * Busca pagamentos agrupados por período, status e tenant para geração de gráficos no dashboard
     * @param usuarioId ID do usuário 
     * @param tenantId ID do tenant
     * @param dataInicio Data de início do período
     * @param dataFim Data de fim do período
     * @param tipoPeriodo Tipo de período (DIA, MES, ANO)
     * @return Mapa com dados agrupados por status e período filtrados por tenant
     */
    Map<String, Map<String, Long>> buscarPorPeriodoAgrupadoComTenant(Long usuarioId, Long tenantId, LocalDate dataInicio, LocalDate dataFim, String tipoPeriodo);
}
