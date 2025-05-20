package cv.ntinnovation.vintipay.application.port.in;

/**
 * Interface para envio de e-mails pelo sistema
 */
public interface EnviaEmailUseCase {

    /**
     * Envia um e-mail para o destinatário especificado
     * 
     * @param destinatario Endereço de e-mail do destinatário
     * @param assunto Assunto do e-mail
     * @param conteudo Conteúdo do e-mail (texto ou HTML)
     * @return true se o e-mail foi enviado com sucesso, false caso contrário
     */
    boolean enviar(String destinatario, String assunto, String conteudo);
}
