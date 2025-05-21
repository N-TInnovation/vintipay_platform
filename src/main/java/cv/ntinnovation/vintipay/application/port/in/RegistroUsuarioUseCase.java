package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;

public interface RegistroUsuarioUseCase {
    void executar(String username, String password, String roles);
    
    void registrarNovoUsuario(String username, String email, String nome, String telefone, String password, String roles);
    
    /**
     * Registra um novo usuário com um plano de assinatura
     * @param username Nome de usuário
     * @param email E-mail do usuário
     * @param nome Nome completo do usuário
     * @param telefone Telefone do usuário
     * @param nif NIF do usuário
     * @param password Senha do usuário (não criptografada)
     * @param roles Papéis do usuário (separados por vírgula)
     * @param planoId ID do plano escolhido
     */
    void registrarNovoUsuarioComPlano(String username, String email, String nome, String telefone, String nif, String password, String roles, Long planoId);
    
    /**
     * Registra um novo usuário sem criar uma assinatura
     * @param username Nome de usuário
     * @param email E-mail do usuário
     * @param nome Nome completo do usuário
     * @param telefone Telefone do usuário
     * @param nif NIF do usuário
     * @param password Senha do usuário (não criptografada)
     * @param roles Papéis do usuário (separados por vírgula)
     * @return O usuário criado
     */
    Usuario registrarNovoUsuarioSemPlano(String username, String email, String nome, String telefone, String nif, String password, String roles);
    
    /**
     * Cria uma assinatura para um usuário já registrado
     * @param username Nome de usuário
     * @param planoId ID do plano a ser assinado
     * @return true se a assinatura foi criada com sucesso, false caso contrário
     */
    boolean criarAssinaturaAposRegistro(String username, Long planoId);
}
