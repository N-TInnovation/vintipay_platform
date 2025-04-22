package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaConfiguracaoPagamentoUsuarioUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import cv.ntinnovation.vintipay.application.port.out.SalvaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GerenciaConfiguracaoPagamentoUsuarioUseCaseImpl implements GerenciaConfiguracaoPagamentoUsuarioUseCase {

    private final BuscaConfiguracaoPagamentoPort buscaConfiguracaoPagamentoPort;
    private final SalvaConfiguracaoPagamentoPort salvaConfiguracaoPagamentoPort;
    private final BuscaUsuario buscaUsuario;

    @Override
    @Transactional
    public ConfiguracaoPagamento criarConfiguracaoParaUsuario(ConfiguracaoPagamento configuracao, Long usuarioId) {
        log.debug("Criando configuração de pagamento para o usuário ID: {}", usuarioId);
        
        // Buscar o usuário
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        
        // Associar o usuário à configuração
        configuracao.setUsuario(usuario);
        configuracao.setIsActive(true);
        
        // Salvar a configuração
        ConfiguracaoPagamento configuracaoSalva = salvaConfiguracaoPagamentoPort.salvar(configuracao);
        log.info("Configuração de pagamento criada com sucesso para o usuário ID: {}", usuarioId);
        
        return configuracaoSalva;
    }

    @Override
    @Transactional
    public ConfiguracaoPagamento atualizarConfiguracaoParaUsuario(Long id, ConfiguracaoPagamento configuracao, Long usuarioId) {
        log.debug("Atualizando configuração de pagamento ID: {} para o usuário ID: {}", id, usuarioId);
        
        // Verificar se a configuração existe e pertence ao usuário
        ConfiguracaoPagamento configuracaoExistente = buscarConfiguracaoPorIdEUsuarioOuLancarExcecao(id, usuarioId);
        
        // Atualizar os campos da configuração
        configuracao.setId(id);
        configuracao.setUsuario(configuracaoExistente.getUsuario());
        configuracao.setUpdatedAt(LocalDateTime.now());
        
        // Salvar a configuração atualizada
        ConfiguracaoPagamento configuracaoAtualizada = salvaConfiguracaoPagamentoPort.salvar(configuracao);
        log.info("Configuração de pagamento ID: {} atualizada com sucesso para o usuário ID: {}", id, usuarioId);
        
        return configuracaoAtualizada;
    }

    @Override
    public List<ConfiguracaoPagamento> buscarConfiguracoesDoUsuario(Long usuarioId) {
        log.debug("Buscando todas as configurações de pagamento para o usuário ID: {}", usuarioId);
        return buscaConfiguracaoPagamentoPort.buscarPorUsuario(usuarioId);
    }

    @Override
    public List<ConfiguracaoPagamento> buscarConfiguracoesAtivasDoUsuario(Long usuarioId) {
        log.debug("Buscando configurações de pagamento ativas para o usuário ID: {}", usuarioId);
        return buscaConfiguracaoPagamentoPort.buscarAtivasPorUsuario(usuarioId);
    }

    @Override
    public Optional<ConfiguracaoPagamento> buscarConfiguracaoPorIdEUsuario(Long id, Long usuarioId) {
        log.debug("Buscando configuração de pagamento ID: {} para o usuário ID: {}", id, usuarioId);
        
        // Buscar a configuração por ID
        Optional<ConfiguracaoPagamento> configuracaoOpt = buscaConfiguracaoPagamentoPort.buscarPorId(id);
        
        // Verificar se a configuração pertence ao usuário
        return configuracaoOpt.filter(config -> 
            config.getUsuario() != null && 
            config.getUsuario().getId().equals(usuarioId)
        );
    }

    @Override
    @Transactional
    public ConfiguracaoPagamento ativarConfiguracao(Long id, Long usuarioId) {
        log.debug("Ativando configuração de pagamento ID: {} para o usuário ID: {}", id, usuarioId);
        
        // Verificar se a configuração existe e pertence ao usuário
        ConfiguracaoPagamento configuracao = buscarConfiguracaoPorIdEUsuarioOuLancarExcecao(id, usuarioId);
        
        // Ativar a configuração
        configuracao.setIsActive(true);
        configuracao.setUpdatedAt(LocalDateTime.now());
        
        // Salvar a configuração atualizada
        ConfiguracaoPagamento configuracaoAtualizada = salvaConfiguracaoPagamentoPort.salvar(configuracao);
        log.info("Configuração de pagamento ID: {} ativada com sucesso para o usuário ID: {}", id, usuarioId);
        
        return configuracaoAtualizada;
    }

    @Override
    @Transactional
    public ConfiguracaoPagamento desativarConfiguracao(Long id, Long usuarioId) {
        log.debug("Desativando configuração de pagamento ID: {} para o usuário ID: {}", id, usuarioId);
        
        // Verificar se a configuração existe e pertence ao usuário
        ConfiguracaoPagamento configuracao = buscarConfiguracaoPorIdEUsuarioOuLancarExcecao(id, usuarioId);
        
        // Desativar a configuração
        configuracao.setIsActive(false);
        configuracao.setUpdatedAt(LocalDateTime.now());
        
        // Salvar a configuração atualizada
        ConfiguracaoPagamento configuracaoAtualizada = salvaConfiguracaoPagamentoPort.salvar(configuracao);
        log.info("Configuração de pagamento ID: {} desativada com sucesso para o usuário ID: {}", id, usuarioId);
        
        return configuracaoAtualizada;
    }
    
    // Métodos auxiliares
    
    private Usuario buscarUsuarioPorId(Long usuarioId) {
        return buscaUsuario.getUser(usuarioId.toString())
                .map(userDetails -> (Usuario) userDetails)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado com o ID: {}", usuarioId);
                    return new UsernameNotFoundException("Usuário não encontrado com o ID: " + usuarioId);
                });
    }
    
    private ConfiguracaoPagamento buscarConfiguracaoPorIdEUsuarioOuLancarExcecao(Long id, Long usuarioId) {
        return buscarConfiguracaoPorIdEUsuario(id, usuarioId)
                .orElseThrow(() -> {
                    log.error("Configuração de pagamento ID: {} não encontrada para o usuário ID: {}", id, usuarioId);
                    return new RuntimeException("Configuração de pagamento não encontrada ou não pertence ao usuário");
                });
    }
}
