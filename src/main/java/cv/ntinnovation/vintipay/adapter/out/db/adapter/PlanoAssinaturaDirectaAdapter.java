package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.mapper.PlanoAssinaturaMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.PlanoAssinaturaRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.port.out.BuscaPlanoAssinaturaDirectaPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador para buscar assinaturas diretamente do repositório
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlanoAssinaturaDirectaAdapter implements BuscaPlanoAssinaturaDirectaPort {

    private final PlanoAssinaturaRepository planoAssinaturaRepository;
    private final PlanoAssinaturaMapper planoAssinaturaMapper;

    @Override
    public Optional<PlanoAssinatura> buscarPorUsuarioId(Long usuarioId) {
        log.debug("Buscando assinatura diretamente pelo ID do usuário: {}", usuarioId);
        return planoAssinaturaRepository.findByUsuarioId(usuarioId)
                .map(planoAssinaturaMapper::toDomain);
    }
}
