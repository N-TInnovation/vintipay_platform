package cv.ntinnovation.vintipay.adapter.out.persistence.adapter;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.ConfiguracaoPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.ConfiguracaoPagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.ConfiguracaoPagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.service.CriptografiaService;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.ConsultaConfiguracaoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConfiguracaoPagamentoAdapter implements
        SalvaConfiguracaoPagamentoPort,
        BuscaConfiguracaoPagamentoPort,
        ConsultaConfiguracaoPagamentoPort {

    private final ConfiguracaoPagamentoRepository repository;
    private final ConfiguracaoPagamentoMapper mapper;
    private final CriptografiaService criptografiaService;

    @Override
    public Optional<ConfiguracaoPagamento> buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ConfiguracaoPagamento> buscarPorCredenciais(String apiKey, String apiSecret) {
        return repository.findByApiKeyAndIsActiveTrue(apiKey)
                .filter(config -> criptografiaService.verificar(apiSecret, config.getApiSecret()))
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ConfiguracaoPagamento> buscarPorApiKey(String clientId) {
        return repository.findByApiKeyAndIsActiveTrue(clientId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ConfiguracaoPagamento> buscarPorPosId(String posId) {
        return repository.findByPosIdAndIsActiveTrue(posId)
                .map(mapper::toDomain);
    }

    @Override
    public List<ConfiguracaoPagamento> buscarTodas() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ConfiguracaoPagamento> buscarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ConfiguracaoPagamento> buscarAtivasPorUsuario(Long usuarioId) {
        return repository.findByUsuarioIdAndIsActiveTrue(usuarioId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<ConfiguracaoPagamento> buscarPorUsuarioEPosId(Long usuarioId, String posId) {
        return repository.findByUsuarioIdAndPosId(usuarioId, posId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ConfiguracaoPagamento> buscarPorUsuarioId(Long usuarioId) {
        List<ConfiguracaoPagamento> configuracoes = buscarAtivasPorUsuario(usuarioId);
        return configuracoes.isEmpty() ? Optional.empty() : Optional.of(configuracoes.get(0));
    }

    @Override
    public ConfiguracaoPagamento salvar(ConfiguracaoPagamento configuracao) {
        log.info("Salvando configuração de pagamento: {}", configuracao);
        try {
            ConfiguracaoPagamentoEntity entity = mapper.toEntity(configuracao);
            log.info("Entidade mapeada: {}", entity);
            
            // Se for uma nova configuração, verificar se o POS ID já existe
            if (entity.getId() == null && repository.existsByPosId(entity.getPosId())) {
                log.error("POS ID já existe: {}", entity.getPosId());
                throw new IllegalArgumentException("Já existe uma configuração com este POS ID: " + entity.getPosId());
            }
            
            ConfiguracaoPagamentoEntity savedEntity = repository.save(entity);
            log.info("Configuração salva com sucesso. ID: {}", savedEntity.getId());
            return mapper.toDomain(savedEntity);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Verificar se é um erro de duplicação de POS ID
            if (e.getMessage() != null && 
                (e.getMessage().contains("UK_POS_ID") || 
                 e.getMessage().contains("UKiq9p0eeolwpce4njfkxd6vhpn") ||
                 e.getMessage().contains("Duplicate entry") && e.getMessage().contains("pos_id"))) {
                log.error("Tentativa de salvar configuração com POS ID duplicado: {}", configuracao.getPosId());
                throw new IllegalArgumentException("Já existe uma configuração com este POS ID: " + configuracao.getPosId(), e);
            }
            log.error("Erro de integridade de dados ao salvar configuração", e);
            throw e;
        } catch (Exception e) {
            log.error("Erro ao salvar configuração de pagamento", e);
            throw e;
        }
    }

    @Override
    public boolean existePorPosId(String posId) {
        log.info("Verificando se existe configuração com POS ID: {}", posId);
        boolean exists = repository.existsByPosId(posId);
        log.info("Resultado da verificação para POS ID {}: {}", posId, exists);
        
        // Verificação adicional para depuração
        if (exists) {
            log.info("Buscando configuração com POS ID: {}", posId);
            Optional<ConfiguracaoPagamentoEntity> config = repository.findByPosId(posId);
            if (config.isPresent()) {
                log.info("Configuração encontrada: ID={}, PosID={}, NomeLoja={}", 
                    config.get().getId(), config.get().getPosId(), config.get().getNomeLoja());
            } else {
                log.warn("INCONSISTÊNCIA DETECTADA: existsByPosId retornou true, mas findByPosId não encontrou a configuração!");
            }
        }
        
        return exists;
    }
    
    @Override
    public boolean existePorPosIdEUsuario(String posId, Long usuarioId) {
        log.info("Verificando se existe configuração com POS ID: {} para o usuário: {}", posId, usuarioId);
        boolean exists = repository.existsByPosIdAndUsuarioId(posId, usuarioId);
        log.info("Resultado da verificação para POS ID {} e usuário {}: {}", posId, usuarioId, exists);
        return exists;
    }
    
    @Override
    public boolean existePorPosIdEOutrosUsuarios(String posId, Long usuarioId) {
        log.info("Verificando se existe configuração com POS ID: {} para outros usuários diferentes de: {}", posId, usuarioId);
        boolean exists = repository.existsByPosIdAndUsuarioIdNot(posId, usuarioId);
        log.info("Resultado da verificação para POS ID {} e outros usuários: {}", posId, exists);
        return exists;
    }
    
    @Override
    public List<ConfiguracaoPagamento> buscarPorTenant(Long tenantId) {
        log.info("Buscando configurações para tenant: {}", tenantId);
        return repository.findByTenantId(tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ConfiguracaoPagamento> buscarPorUsuarioETenant(Long usuarioId, Long tenantId) {
        log.info("Buscando configurações para usuário: {} e tenant: {}", usuarioId, tenantId);
        return repository.findByUsuarioIdAndTenantId(usuarioId, tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
