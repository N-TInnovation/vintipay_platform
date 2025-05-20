package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;

import java.util.Optional;

/**
 * Interface para buscas relacionadas a usuários
 */
public interface BuscaUsuarioUseCase {

    /**
     * Busca um usuário pelo e-mail
     * @param email E-mail do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<Usuario> buscarPorEmail(String email);
    
    /**
     * Busca um usuário pelo token de recuperação de senha
     * @param token Token de recuperação de senha
     * @return Optional contendo o usuário se encontrado
     */
    Optional<Usuario> buscarPorResetToken(String token);
}
