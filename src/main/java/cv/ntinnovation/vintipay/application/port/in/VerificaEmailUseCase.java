package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import java.util.Optional;

/**
 * Interface para o caso de uso de verificação de email
 */
public interface VerificaEmailUseCase {
    
    /**
     * Cria um token de verificação para o usuário e envia um email
     * @param usuario Usuário para o qual gerar o token
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    boolean criarTokenVerificacaoEEnviarEmail(Usuario usuario);
    
    /**
     * Verifica um token de verificação de email
     * @param token Token a ser verificado
     * @return o usuário se o token for válido, vazio caso contrário
     */
    Optional<Usuario> validarTokenVerificacao(String token);
    
    /**
     * Reenvia o email de verificação para um usuário
     * @param email Email do usuário
     * @return true se o email foi reenviado com sucesso, false caso contrário
     */
    boolean reenviarEmailVerificacao(String email);
}
