package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface BuscaUsuarioPort {
    Optional<Usuario> getUser(String username);
    
    /**
     * Busca um usuário pelo seu nome de usuário
     * @param username Nome de usuário a ser buscado
     * @return Optional contendo o usuário se encontrado, ou vazio se não existir
     */
    Optional<Usuario> buscarPorUsername(String username);
    
    /**
     * Busca um usuário pelo seu ID
     * @param id ID do usuário a ser buscado
     * @return Optional contendo o usuário se encontrado, ou vazio se não existir
     */
    Optional<Usuario> buscarPorId(Long id);
    
    /**
     * Busca um usuário pelo seu e-mail
     * @param email E-mail do usuário a ser buscado
     * @return Optional contendo o usuário se encontrado, ou vazio se não existir
     */
    Optional<Usuario> buscarPorEmail(String email);
    
    /**
     * Busca um usuário pelo token de recuperação de senha
     * @param token Token de recuperação de senha
     * @return Optional contendo o usuário se encontrado, ou vazio se não existir
     */
    Optional<Usuario> buscarPorResetToken(String token);
    
    /**
     * Busca um usuário pelo token de verificação de email
     * @param token Token de verificação de email
     * @return Optional contendo o usuário se encontrado, ou vazio se não existir
     */
    Optional<Usuario> buscarPorEmailVerificationToken(String token);

    /**
     * Lista todos os usuários do sistema
     * @return Lista de usuários
     */
    List<Usuario> listarTodos();
}
