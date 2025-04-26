package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;

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
}
