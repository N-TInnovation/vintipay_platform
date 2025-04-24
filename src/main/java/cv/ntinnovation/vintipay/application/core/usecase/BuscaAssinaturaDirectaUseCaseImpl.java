package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.port.in.BuscaAssinaturaDirectaUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaPlanoAssinaturaDirectaPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementação do caso de uso para buscar assinaturas diretamente
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BuscaAssinaturaDirectaUseCaseImpl implements BuscaAssinaturaDirectaUseCase {

    private final BuscaPlanoAssinaturaDirectaPort buscaPlanoAssinaturaDirectaPort;

    @Override
    public Optional<PlanoAssinatura> buscarAssinaturaPorUsuarioId(Long usuarioId) {
        log.info("Buscando assinatura diretamente pelo ID do usuário: {}", usuarioId);
        return buscaPlanoAssinaturaDirectaPort.buscarPorUsuarioId(usuarioId);
    }
}
