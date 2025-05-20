package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.port.in.NotificacaoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BrevoServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Implementação do caso de uso de notificações
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoUseCaseImpl implements NotificacaoUseCase {

    private final BrevoServicePort brevoServicePort;
    
    @Override
    public boolean enviarEmail(String destinatario, String toName, String assunto, String conteudo) {
        try {
            log.debug("Enviando email para {}, assunto: {}", destinatario, assunto);
            brevoServicePort.sendEmail(destinatario, toName, assunto, conteudo);
            return true;
        } catch (Exception e) {
            log.error("Erro ao enviar email para {}: {}", destinatario, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean enviarEmailPagamento(String destinatario, String destinatarioNome, String referenciaPagamento, Double valor, String descricao, String urlVisualizarRecibo) {
        try {
            String valorFormatado = NumberFormat.getCurrencyInstance(new Locale("pt", "CV")).format(valor);
            String assunto = "Confirmação de Pagamento - VintiPay";
            
            String conteudo = buildEmailConfirmacaoPagamento(referenciaPagamento, valorFormatado, descricao, urlVisualizarRecibo);
            
            log.debug("Enviando email de pagamento para {}, nome: {}, referência: {}",
                    destinatario, destinatarioNome, referenciaPagamento);
            
            brevoServicePort.sendEmail(destinatario, destinatarioNome, assunto, conteudo);
            return true;
        } catch (Exception e) {
            log.error("Erro ao enviar email de confirmação para {}: {}", 
                    destinatario, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean notificarComerciante(String emailComerciante, String nomeComerciante, String nomeCliente, 
                                        String emailCliente, String referenciaPagamento, Double valor) {
        try {
            String valorFormatado = NumberFormat.getCurrencyInstance(new Locale("pt", "CV")).format(valor);
            String assunto = "Nova Transação Recebida - VintiPay";
            
            String conteudo = buildEmailNotificacaoComerciante(
                    nomeComerciante, 
                    nomeCliente, 
                    emailCliente, 
                    referenciaPagamento, 
                    valorFormatado,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );
            
            log.debug("Enviando notificação para comerciante {}, referência pagamento: {}", 
                    emailComerciante, referenciaPagamento);
            
            brevoServicePort.sendEmail(emailComerciante, nomeComerciante, assunto, conteudo);
            return true;
        } catch (Exception e) {
            log.error("Erro ao enviar notificação ao comerciante {}: {}", 
                    emailComerciante, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Constrói o HTML para o email de confirmação de pagamento
     */
    private String buildEmailConfirmacaoPagamento(String referenciaPagamento, String valor, String descricao, String urlVisualizarRecibo) {
        return "<html>" +
                "<head>" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "    .header { background-color: #3b91d3; color: white; padding: 10px 20px; text-align: center; }" +
                "    .content { padding: 20px; border: 1px solid #ddd; border-top: none; }" +
                "    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #777; }" +
                "    .btn { display: inline-block; padding: 10px 20px; background-color: #3b91d3; color: white; " +
                "           text-decoration: none; border-radius: 4px; margin: 20px 0; text-align: center; }" +
                "    .highlight { font-weight: bold; color: #3b91d3; }" +
                "    .actions { text-align: center; margin: 25px 0; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='container'>" +
                "    <div class='header'>" +
                "      <h2>VintiPay - Confirmação de Pagamento</h2>" +
                "    </div>" +
                "    <div class='content'>" +
                "      <p>Prezado(a) cliente,</p>" +
                "      <p>O seu pagamento foi processado com sucesso!</p>" +
                "      <p>Detalhes da transação:</p>" +
                "      <ul>" +
                "        <li><strong>Referência:</strong> <span class='highlight'>" + referenciaPagamento + "</span></li>" +
                "        <li><strong>Valor:</strong> <span class='highlight'>" + valor + "</span></li>" +
                (descricao != null && !descricao.isEmpty() ? 
                "        <li><strong>Produto/Serviço:</strong> <span class='highlight'>" + descricao + "</span></li>" : "") +
                "      </ul>" +
                "      <div class='actions'>" +
                "        <a href='" + urlVisualizarRecibo + "' class='btn'>Visualizar Recibo</a>" +
                "      </div>" +
                "      <p>Você também pode acessar o seu recibo a qualquer momento através do link abaixo:</p>" +
                "      <p><a href='" + urlVisualizarRecibo + "'>" + urlVisualizarRecibo + "</a></p>" +
                "      <p>Obrigado por utilizar nossos serviços.</p>" +
                "    </div>" +
                "    <div class='footer'>" +
                "      <p>Este é um email automático. Por favor, não responda.</p>" +
                "      <p>&copy; " + java.time.Year.now().getValue() + " VintiPay. Todos os direitos reservados.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }
    
    /**
     * Constrói o HTML para o email de notificação ao comerciante
     */
    private String buildEmailNotificacaoComerciante(String nomeComerciante, String nomeCliente, 
                                                  String emailCliente, String referencia, 
                                                  String valor, String dataHora) {
        return "<html>" +
                "<head>" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "    .header { background-color: #4CAF50; color: white; padding: 10px 20px; text-align: center; }" +
                "    .content { padding: 20px; border: 1px solid #ddd; border-top: none; }" +
                "    .transaction-details { background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 15px 0; }" +
                "    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #777; }" +
                "    .highlight { font-weight: bold; color: #4CAF50; }" +
                "    table { width: 100%; border-collapse: collapse; }" +
                "    table td, table th { padding: 8px; border-bottom: 1px solid #ddd; }" +
                "    table th { text-align: left; background-color: #f2f2f2; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='container'>" +
                "    <div class='header'>" +
                "      <h2>Nova Transação Recebida</h2>" +
                "    </div>" +
                "    <div class='content'>" +
                "      <p>Olá <strong>" + nomeComerciante + "</strong>,</p>" +
                "      <p>Você recebeu um novo pagamento através da <strong>VintiPay</strong>.</p>" +
                "      <div class='transaction-details'>" +
                "        <h3>Detalhes da Transação</h3>" +
                "        <table>" +
                "          <tr>" +
                "            <th>Cliente:</th>" +
                "            <td>" + nomeCliente + " (" + emailCliente + ")</td>" +
                "          </tr>" +
                "          <tr>" +
                "            <th>Referência:</th>" +
                "            <td><span class='highlight'>" + referencia + "</span></td>" +
                "          </tr>" +
                "          <tr>" +
                "            <th>Valor:</th>" +
                "            <td><span class='highlight'>" + valor + " ECV</span></td>" +
                "          </tr>" +
                "          <tr>" +
                "            <th>Data/Hora:</th>" +
                "            <td>" + dataHora + "</td>" +
                "          </tr>" +
                "          <tr>" +
                "            <th>Status:</th>" +
                "            <td><span style='color: #4CAF50; font-weight: bold;'>PAGO</span></td>" +
                "          </tr>" +
                "        </table>" +
                "      </div>" +
                "      <p>Acesse o seu painel administrativo para mais detalhes sobre esta transação.</p>" +
                "    </div>" +
                "    <div class='footer'>" +
                "      <p>Este é um email automático. Por favor, não responda.</p>" +
                "      <p>&copy; " + java.time.Year.now().getValue() + " VintiPay. Todos os direitos reservados.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }
}
