package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.PlanoAssinaturaMapper;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.UsuarioMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.PlanoAssinaturaRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.out.BuscaPlanoAssinaturaPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaPlanoAssinaturaPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlanoAssinaturaAdapter implements BuscaPlanoAssinaturaPort, SalvaPlanoAssinaturaPort {

    private final PlanoAssinaturaRepository planoAssinaturaRepository;
    private final PlanoAssinaturaMapper planoAssinaturaMapper;
    private final UsuarioMapper usuarioMapper;

    @Override
    public Optional<PlanoAssinatura> buscarPorUsuario(Usuario usuario) {
        UsuarioEntity usuarioEntity = usuarioMapper.toEntity(usuario);
        return planoAssinaturaRepository.findByUsuario(usuarioEntity)
                .map(planoAssinaturaMapper::toDomain);
    }

    @Override
    public Optional<PlanoAssinatura> buscarPorStripeSubscriptionId(String stripeSubscriptionId) {
        return planoAssinaturaRepository.findByStripeSubscriptionId(stripeSubscriptionId)
                .map(planoAssinaturaMapper::toDomain);
    }
    
    @Override
    public Optional<PlanoAssinatura> buscarPorId(Long id) {
        log.debug("Buscando assinatura por ID: {}", id);
        return planoAssinaturaRepository.findById(id)
                .map(planoAssinaturaMapper::toDomain);
    }

    @Override
    public List<PlanoAssinatura> buscarPorStatus(PlanoAssinatura.StatusAssinatura status) {
        var statusEntity = getStatusEntity(status);
        return planoAssinaturaRepository.findByStatus(statusEntity)
                .stream()
                .map(planoAssinaturaMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    private cv.ntinnovation.vintipay.adapter.out.db.entity.PlanoAssinaturaEntity.StatusAssinatura getStatusEntity(PlanoAssinatura.StatusAssinatura status) {
        switch (status) {
            case ATIVA:
                return cv.ntinnovation.vintipay.adapter.out.db.entity.PlanoAssinaturaEntity.StatusAssinatura.ATIVA;
            case TRIAL:
                return cv.ntinnovation.vintipay.adapter.out.db.entity.PlanoAssinaturaEntity.StatusAssinatura.TRIAL;
            case CANCELADA:
                return cv.ntinnovation.vintipay.adapter.out.db.entity.PlanoAssinaturaEntity.StatusAssinatura.CANCELADA;
            case PENDENTE:
                return cv.ntinnovation.vintipay.adapter.out.db.entity.PlanoAssinaturaEntity.StatusAssinatura.PENDENTE;
            case EXPIRADA:
                return cv.ntinnovation.vintipay.adapter.out.db.entity.PlanoAssinaturaEntity.StatusAssinatura.EXPIRADA;
            default:
                return null;
        }
    }

    @Override
    public PlanoAssinatura salvar(PlanoAssinatura planoAssinatura) {
        var planoAssinaturaEntity = planoAssinaturaMapper.toEntity(planoAssinatura);
        var savedEntity = planoAssinaturaRepository.save(planoAssinaturaEntity);
        return planoAssinaturaMapper.toDomain(savedEntity);
    }
}
