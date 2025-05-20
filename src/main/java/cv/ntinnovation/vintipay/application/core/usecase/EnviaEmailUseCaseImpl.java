package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.port.in.EnviaEmailUseCase;
import cv.ntinnovation.vintipay.application.port.out.BrevoServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnviaEmailUseCaseImpl implements EnviaEmailUseCase {

    private final BrevoServicePort brevoServicePort;
    
    @Override
    public boolean enviar(String destinatario, String assunto, String conteudo) {
        try {
            log.info("Enviando e-mail para: {}, assunto: {}", destinatario, assunto);
            
            // Utiliza o serviço Brevo para envio do email
            brevoServicePort.sendEmail(destinatario, "Usuário", assunto, conteudo);
            
            log.info("E-mail enviado com sucesso para: {}", destinatario);
            return true;
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail para: {}", destinatario, e);
            return false;
        }
    }
}
