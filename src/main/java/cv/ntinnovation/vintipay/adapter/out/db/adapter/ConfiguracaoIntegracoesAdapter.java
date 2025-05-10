package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.mapper.ConfiguracaoIntegracoesMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.ConfiguracaoIntegracoesRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoIntegracoes;
import cv.ntinnovation.vintipay.application.port.out.ConfiguracaoIntegracoesPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfiguracaoIntegracoesAdapter implements ConfiguracaoIntegracoesPort {
    private final ConfiguracaoIntegracoesRepository configuracaoIntegracoesRepository;
    private final ConfiguracaoIntegracoesMapper configuracaoIntegracoesMapper;

    @Override
    public ConfiguracaoIntegracoes salvar(ConfiguracaoIntegracoes configuracaoIntegracoes) {
        log.info("Salvando configuração de aplicação: {}", configuracaoIntegracoes);
        try {
            // Converte para entidade
            var entity = configuracaoIntegracoesMapper.toEntity(configuracaoIntegracoes);
            
            // Importante: garantir que a entidade tem uma referência válida para configuracaoPagamento
            if (configuracaoIntegracoes.getConfiguracaoPagamento() != null &&
                configuracaoIntegracoes.getConfiguracaoPagamento().getId() != null) {
                
                Long configuracaoPagamentoId = configuracaoIntegracoes.getConfiguracaoPagamento().getId();
                log.info("Associando à configuração de pagamento ID: {}", configuracaoPagamentoId);
            }
            
            // Salva a entidade
            var savedEntity = configuracaoIntegracoesRepository.save(entity);
            configuracaoIntegracoes = configuracaoIntegracoesMapper.toDomain(savedEntity);
            log.info("Configuração de aplicação salva com sucesso. ID: {}", savedEntity.getId());
        } catch (Exception e) {
            log.error("Erro ao salvar configuração de aplicação", e);
            throw e;
        }

        return configuracaoIntegracoes;
    }

    @Override
    public List<ConfiguracaoIntegracoes> buscarTodas(Long usuarioId) {
        var list = configuracaoIntegracoesRepository.findByUsuarioId(usuarioId);
        return list.stream()
                .map(configuracaoIntegracoesMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConfiguracaoIntegracoes> buscarTodasPorConfiguracaoPagamento(Long id) {
        return configuracaoIntegracoesRepository.findByConfiguracaoPagamentoId(id).stream()
                .map(configuracaoIntegracoesMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConfiguracaoIntegracoes> buscarAtivasPorUsuario(Long usuarioId) {
        log.info("Buscando integrações ativas para o usuário ID: {}", usuarioId);
        var integracoes = configuracaoIntegracoesRepository.findByUsuarioIdAndActive(usuarioId);
        log.info("Encontradas {} integrações ativas", integracoes.size());
        
        return integracoes.stream()
                .map(configuracaoIntegracoesMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ConfiguracaoIntegracoes buscarPorId(Long id) {
        return configuracaoIntegracoesRepository.findById(id).map(configuracaoIntegracoesMapper::toDomain).orElse(null);
    }

    @Override
    public Optional<ConfiguracaoIntegracoes> buscarPorIdUsuario(Long id, Long usuarioId) {
        return configuracaoIntegracoesRepository.findByIdAndUsuarioId(id, usuarioId).map(configuracaoIntegracoesMapper::toDomain);
    }

    @Override
    public boolean deletarPorId(Long id) {
        configuracaoIntegracoesRepository.deleteById(id);
        return false;
    }

    @Override
    public boolean deletarPorIdUsuario(Long id, Long usuarioId) {
        configuracaoIntegracoesRepository.deleteByIdAndUsuarioId(id, usuarioId);
        return true;
    }

    @Override
    public boolean existeTipoIntegracaoEmConfiguracaoPagamentoPorUsuario(Long id, String tipoIntegracao, Long usuarioId) {
        return configuracaoIntegracoesRepository.existsByConfiguracaoPagamentoIdAndUsuarioIdAndName(id, usuarioId, tipoIntegracao).isPresent();
    }

}
