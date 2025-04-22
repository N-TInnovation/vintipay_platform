package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface GerenciaConfiguracaoPagamentoUsuarioUseCase {
    
    /**
     * Cria uma nova configuração de pagamento para um usuário específico
     * 
     * @param configuracao A configuração de pagamento a ser criada
     * @param usuarioId O ID do usuário ao qual a configuração pertence
     * @return A configuração de pagamento criada
     */
    ConfiguracaoPagamento criarConfiguracaoParaUsuario(ConfiguracaoPagamento configuracao, Long usuarioId);
    
    /**
     * Atualiza uma configuração de pagamento existente para um usuário específico
     * 
     * @param id O ID da configuração de pagamento a ser atualizada
     * @param configuracao A configuração de pagamento atualizada
     * @param usuarioId O ID do usuário ao qual a configuração pertence
     * @return A configuração de pagamento atualizada
     */
    ConfiguracaoPagamento atualizarConfiguracaoParaUsuario(Long id, ConfiguracaoPagamento configuracao, Long usuarioId);
    
    /**
     * Busca todas as configurações de pagamento de um usuário específico
     * 
     * @param usuarioId O ID do usuário
     * @return Lista de configurações de pagamento do usuário
     */
    List<ConfiguracaoPagamento> buscarConfiguracoesDoUsuario(Long usuarioId);
    
    /**
     * Busca todas as configurações de pagamento ativas de um usuário específico
     * 
     * @param usuarioId O ID do usuário
     * @return Lista de configurações de pagamento ativas do usuário
     */
    List<ConfiguracaoPagamento> buscarConfiguracoesAtivasDoUsuario(Long usuarioId);
    
    /**
     * Busca uma configuração de pagamento específica de um usuário
     * 
     * @param id O ID da configuração de pagamento
     * @param usuarioId O ID do usuário
     * @return A configuração de pagamento, se encontrada
     */
    Optional<ConfiguracaoPagamento> buscarConfiguracaoPorIdEUsuario(Long id, Long usuarioId);
    
    /**
     * Ativa uma configuração de pagamento para um usuário específico
     * 
     * @param id O ID da configuração de pagamento
     * @param usuarioId O ID do usuário
     * @return A configuração de pagamento ativada
     */
    ConfiguracaoPagamento ativarConfiguracao(Long id, Long usuarioId);
    
    /**
     * Desativa uma configuração de pagamento para um usuário específico
     * 
     * @param id O ID da configuração de pagamento
     * @param usuarioId O ID do usuário
     * @return A configuração de pagamento desativada
     */
    ConfiguracaoPagamento desativarConfiguracao(Long id, Long usuarioId);
}
