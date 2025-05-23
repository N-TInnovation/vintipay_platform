package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface GerenciaUsuarioUseCase {
    
    /**
     * Cria um novo usuário no sistema
     * 
     * @param usuario Dados do usuário a ser criado
     * @return Usuário criado com ID gerado
     */
    Usuario criarUsuario(Usuario usuario);
    
    /**
     * Atualiza um usuário existente
     * 
     * @param usuario Dados do usuário a ser atualizado
     * @return Usuário atualizado
     */
    Usuario atualizarUsuario(Usuario usuario);
    
    /**
     * Busca um usuário pelo ID
     * 
     * @param id ID do usuário
     * @return Usuário encontrado ou vazio se não existir
     */
    Optional<Usuario> buscarPorId(Long id);
    
    /**
     * Busca um usuário pelo nome de usuário
     * 
     * @param username Nome de usuário
     * @return Usuário encontrado ou vazio se não existir
     */
    Optional<Usuario> buscarPorUsername(String username);
    
    /**
     * Busca um usuário pelo email
     * 
     * @param email Email do usuário
     * @return Usuário encontrado ou vazio se não existir
     */
    Optional<Usuario> buscarPorEmail(String email);
    
    /**
     * Lista todos os usuários do sistema
     * 
     * @return Lista de usuários
     */
    List<Usuario> listarTodos();
    
    /**
     * Ativa um usuário
     * 
     * @param id ID do usuário
     * @return Usuário ativado
     */
    Usuario ativarUsuario(Long id);
    
    /**
     * Desativa um usuário
     * 
     * @param id ID do usuário
     * @return Usuário desativado
     */
    Usuario desativarUsuario(Long id);

    /**
     * Altera a senha de um usuário
     *
     * @param id ID do usuário
     * @param senhaCriptografada Senha criptografada
     * @return Usuário com senha alterada
     */
    Usuario alterarSenha(Long id, String senhaCriptografada);
}
