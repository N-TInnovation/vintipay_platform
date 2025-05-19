package cv.ntinnovation.vintipay.adapter.out.client.api.brevo;

import cv.ntinnovation.vintipay.adapter.out.client.api.brevo.dto.*;
import cv.ntinnovation.vintipay.application.port.out.BrevoServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BrevoService implements BrevoServicePort {
    private final Brevo brevoClient;

    @Value("${brevo.email.from}")
    private String emailFrom;

    @Value("${brevo.api.key}")
    private  String brevoApiKey;

    @Override
    public void sendEmail(String to, String toName, String subject, String htmlContent) {
        // Usando o endpoint de email transacional (/v3/smtp/email) 
        // ao invés do endpoint de campanhas
        BrevoTransactionalEmailRequest request = BrevoTransactionalEmailRequest.builder()
                .sender(BrevoSender.builder()
                        .name("VintiPay")
                        .email(emailFrom)
                        .build())
                .to(Collections.singletonList(
                        BrevoRecipient.builder()
                                .email(to)
                                .name(toName)  // Nome opcional
                                .build()
                ))
                .subject(subject)
                .htmlContent(htmlContent)
                .build();

        try {
            ResponseEntity<BrevoTransactionalEmailResponse> response =
                    brevoClient.sendTransactionalEmail(brevoApiKey, request);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Email enviado com sucesso. MessageId: {}", response.getBody().getMessageId());
            } else {
                log.error("Falha ao enviar email. Status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Erro ao enviar email: ", e);
        }
    }
    
    /**
     * Método para enviar emails em formato de campanha
     * Útil para enviar para várias listas de destinatários
     */
    public void sendCampaignEmail(String subject, String htmlContent, List<Integer> listIds) {
        BrevoEmailRequest request = BrevoEmailRequest.builder()
                .name("Campaign: " + subject)
                .subject(subject)
                .sender(BrevoSender.builder()
                        .name("VintiPay")
                        .email(emailFrom)
                        .build())
                .type("classic")
                .htmlContent(htmlContent)
                .recipients(BrevoRecipients.builder()
                        .listIds(listIds)
                        .build())
                .build();

        try {
            ResponseEntity<BrevoEmailResponse> response =
                    brevoClient.sendEmailCampaign(brevoApiKey, request);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Campanha de email enviada com sucesso. ID: {}", response.getBody().getId());
            } else {
                log.error("Falha ao enviar campanha de email. Status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Erro ao enviar campanha de email: ", e);
        }
    }
}
