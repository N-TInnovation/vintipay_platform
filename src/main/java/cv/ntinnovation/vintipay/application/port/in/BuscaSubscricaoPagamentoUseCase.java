package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.SubscricaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusSubscricaoPagamentoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BuscaSubscricaoPagamentoUseCase {
    
    /**
     * Busca assinaturas por tenant e ambiente com paginação
     * 
     * @param pageable configuração de paginação
     * @param tenantId ID do tenant
     * @param ambiente ambiente (PRODUCAO ou HOMOLOGACAO)
     * @return página de assinaturas
     */
    Page<SubscricaoPagamento> buscarPorTenantEAmbiente(Pageable pageable, Long tenantId, AmbienteEnum ambiente);
    
    /**
     * Busca assinaturas por tenant, ambiente e status com paginação
     * 
     * @param pageable configuração de paginação
     * @param tenantId ID do tenant
     * @param status status da assinatura
     * @param ambiente ambiente (PRODUCAO ou HOMOLOGACAO)
     * @return página de assinaturas
     */
    Page<SubscricaoPagamento> buscarPorTenantEAmbienteEStatus(Pageable pageable, Long tenantId, StatusSubscricaoPagamentoEnum status, AmbienteEnum ambiente);
}
