package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.port.in.ConsultaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.ConsultaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementação do caso de uso para consultas relacionadas a configurações de pagamento
 */
@Service
@RequiredArgsConstructor
public class ConsultaConfiguracaoPagamentoUseCaseImpl implements ConsultaConfiguracaoPagamentoUseCase {
    
    private final ConsultaConfiguracaoPagamentoPort consultaConfiguracaoPagamentoPort;
    
    @Override
    public boolean existePorPosId(String posId) {
        return consultaConfiguracaoPagamentoPort.existePorPosId(posId);
    }
    
    @Override
    public boolean existePorPosIdEUsuario(String posId, Long usuarioId) {
        return consultaConfiguracaoPagamentoPort.existePorPosIdEUsuario(posId, usuarioId);
    }
    
    @Override
    public boolean existePorPosIdEOutrosUsuarios(String posId, Long usuarioId) {
        return consultaConfiguracaoPagamentoPort.existePorPosIdEOutrosUsuarios(posId, usuarioId);
    }
}
