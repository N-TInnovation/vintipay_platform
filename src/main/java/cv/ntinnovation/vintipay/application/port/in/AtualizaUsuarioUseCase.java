package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;

/**
 * Interface para operações de atualização de dados de usuários
 */
public interface AtualizaUsuarioUseCase {

    /**
     * Atualiza os dados de um usuário
     * @param usuario Dados atualizados do usuário
     * @return Usuário atualizado
     */
    Usuario atualizar(Usuario usuario);
}
