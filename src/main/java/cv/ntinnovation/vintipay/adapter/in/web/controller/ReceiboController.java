package cv.ntinnovation.vintipay.adapter.in.web.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.BuscaPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.NotificacaoUseCase;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/recibo")
@RequiredArgsConstructor
public class ReceiboController {

    private final BuscaPagamentoUseCase pagamentoUseCase;
    private final NotificacaoUseCase notificacaoUseCase;
    private final TemplateEngine templateEngine;
    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoUseCase ;

    /**
     * Gera um QR code como imagem base64 para uso em templates HTML
     */
    private String gerarQRCodeBase64(String url) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 250, 250);
            
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngOutputStream.toByteArray());
        } catch (Exception e) {
            log.error("Erro ao gerar QR code base64: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * Gera e faz download do recibo em PDF
     */
    @GetMapping("/download/pdf/{referencia}")
    public void downloadPdf(@PathVariable String referencia, HttpServletResponse response) {
        try {
            Optional<Pagamento> pagamentoOptional = pagamentoUseCase.buscarPorReferencia(referencia);
            if (pagamentoOptional.isEmpty()) {
                response.sendError(HttpStatus.NOT_FOUND.value(), "Pagamento não encontrado");
                return;
            }

            Pagamento pagamento = pagamentoOptional.get();

            // Prepara o contexto com os dados para o template
            Context context = new Context();
            context.setVariable("pagamento", pagamento);
            context.setVariable("dataFormatada", 
                pagamento.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                
            // Busca informações da loja/configuração de pagamento
            ConfiguracaoPagamento config =
                buscaConfiguracaoPagamentoUseCase.buscarPorPosId(pagamento.getIntencaoPagamento().getPosId());
            if (config != null) {
                context.setVariable("loja", config);
            }
                
            // Gera o QR code e converte para base64
            String qrCodeBase64 = gerarQRCodeBase64("https://vintipay.cv/validar/payment/reference/" + pagamento.getReference());
            context.setVariable("qrCodeBase64", qrCodeBase64);

            // Processa o template HTML do recibo
            String htmlContent = templateEngine.process("recibo/recibo_pdf", context);

            // Configura o PDF renderer
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();

            // Configura a resposta HTTP para download
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=recibo-" + referencia + ".pdf");

            // Gera o PDF para o output stream da resposta
            renderer.createPDF(response.getOutputStream());
            response.getOutputStream().flush();

        } catch (Exception e) {
            log.error("Erro ao gerar PDF do recibo: {}", e.getMessage(), e);
            try {
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Erro ao gerar o recibo: " + e.getMessage());
            } catch (Exception ex) {
                log.error("Erro ao enviar resposta de erro: {}", ex.getMessage());
            }
        }
    }

    /**
     * Gera QR code para validação do pagamento
     */
    @GetMapping("/api/qrcode/{referencia}")
    public void generateQRCode(@PathVariable String referencia, HttpServletResponse response) {
        try {
            // Gera uma URL de validação que pode ser usada para verificar o recibo
            String validationUrl = "https://vintipay.cv/validar/" + referencia;
            
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(validationUrl, BarcodeFormat.QR_CODE, 250, 250);
            
            // Configura resposta
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            
            // Escreve a imagem do QR code na resposta
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", response.getOutputStream());
            response.getOutputStream().flush();
            
        } catch (Exception e) {
            log.error("Erro ao gerar QR code: {}", e.getMessage(), e);
            try {
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Erro ao gerar QR code: " + e.getMessage());
            } catch (Exception ex) {
                log.error("Erro ao enviar resposta de erro: {}", ex.getMessage());
            }
        }
    }

    /**
     * Exibe uma versão web do recibo para visualização com URL amigável sem o prefixo 'view'
     * Acesso direto via /recibo/{id}
     */
    @GetMapping("/{id}")
    public String visualizarReciboPorId(@PathVariable String id, Map<String, Object> model) {
        try {
            log.info("Requisição de visualização do recibo com ID: {}", id);
            
            // Busca o pagamento
            Optional<Pagamento> pagamentoOptional = pagamentoUseCase.buscarPorReferencia(id);
            if (pagamentoOptional.isEmpty()) {
                log.warn("Pagamento não encontrado para o ID: {}", id);
                model.put("error", "Pagamento não encontrado");
                return "error/404";
            }

            Pagamento pagamento = pagamentoOptional.get();
            
            // Adiciona dados do pagamento ao modelo
            model.put("pagamento", pagamento);
            model.put("dataFormatada", 
                pagamento.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                
            // Busca informações da loja/configuração de pagamento
            ConfiguracaoPagamento config =
                buscaConfiguracaoPagamentoUseCase.buscarPorPosId(pagamento.getIntencaoPagamento().getPosId());
            if (config != null) {
                model.put("loja", config);
            }
            
            // Gera o QR code e converte para base64
            String qrCodeBase64 = gerarQRCodeBase64("https://vintipay.cv/validar/payment/reference/" + pagamento.getReference());
            model.put("qrCodeBase64", qrCodeBase64);
            
            // Retorna o template com o recibo
            return "recibo/visualizar";
            
        } catch (Exception e) {
            log.error("Erro ao exibir recibo com ID {}: {}", id, e.getMessage(), e);
            model.put("error", "Erro ao processar o recibo: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * Exibe uma versão web do recibo para visualização
     */
    @GetMapping("/view/{referencia}")
    public String visualizarRecibo(@PathVariable String referencia, Map<String, Object> model) {
        try {
            // Busca o pagamento
            Optional<Pagamento> pagamentoOptional = pagamentoUseCase.buscarPorReferencia(referencia);
            if (pagamentoOptional.isEmpty()) {
                model.put("error", "Pagamento não encontrado");
                return "error/404";
            }

            Pagamento pagamento = pagamentoOptional.get();
            
            // Adiciona dados do pagamento ao modelo
            model.put("pagamento", pagamento);
            model.put("dataFormatada", 
                pagamento.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                
            // Busca informações da loja/configuração de pagamento
            ConfiguracaoPagamento config =
                buscaConfiguracaoPagamentoUseCase.buscarPorPosId(pagamento.getIntencaoPagamento().getPosId());
            if (config != null) {
                model.put("loja", config);
            }
            
            // Gera o QR code e converte para base64
            String qrCodeBase64 = gerarQRCodeBase64("https://vintipay.cv/validar/payment/reference/" + pagamento.getReference());
            model.put("qrCodeBase64", qrCodeBase64);
            
            // Retorna o template com o recibo
            return "recibo/visualizar";
            
        } catch (Exception e) {
            log.error("Erro ao exibir recibo: {}", e.getMessage(), e);
            model.put("error", "Erro ao processar o recibo: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * Envia o recibo por email
     */
    @PostMapping("/enviar-email")
    @ResponseBody
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<Map<String, Object>> enviarPorEmail(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = request.get("email");
            String referencia = request.get("referencia");
            
            if (email == null || email.trim().isEmpty() || referencia == null || referencia.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email ou referência inválidos");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Busca o pagamento
            Optional<Pagamento> pagamentoOptional = pagamentoUseCase.buscarPorReferencia(referencia);
            if (pagamentoOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "Pagamento não encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Pagamento pagamento = pagamentoOptional.get();
            
            // Prepara o contexto com os dados para o template
            Context context = new Context();
            context.setVariable("pagamento", pagamento);
            context.setVariable("dataFormatada", 
                pagamento.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                
            // Busca informações da loja/configuração de pagamento
            ConfiguracaoPagamento config =
                buscaConfiguracaoPagamentoUseCase.buscarPorPosId(pagamento.getIntencaoPagamento().getPosId());
            if (config != null) {
                context.setVariable("loja", config);
            }
            
            // Processa o template HTML do recibo
            String htmlContent = templateEngine.process("recibo/recibo_email", context);
            
            // Envia o email com o recibo
            boolean enviado = notificacaoUseCase.enviarEmail(
                email,
                pagamento.getIntencaoPagamento().getContaPagamento().getName(),
                "Recibo de Pagamento - VintiPay #" + referencia,
                htmlContent
            );
            
            if (enviado) {
                response.put("success", true);
                response.put("message", "Recibo enviado com sucesso para " + email);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Falha ao enviar o recibo");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            log.error("Erro ao enviar recibo por email: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Erro ao processar a solicitação: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
