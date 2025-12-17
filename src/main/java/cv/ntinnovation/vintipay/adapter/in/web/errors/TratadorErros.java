package cv.ntinnovation.vintipay.adapter.in.web.errors;

import cv.ntinnovation.vintipay.application.core.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@ControllerAdvice
public class TratadorErros {

    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(TratadorErros.class);

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, RedirectAttributes redirectAttributes) {
        log.error("Arquivo excede o tamanho máximo permitido: {}", ex.getMessage());
        
        long maxSize = ex.getMaxUploadSize();
        String maxSizeFormatted = maxSize > 0 ? formatFileSize(maxSize) : "10MB";
        
        redirectAttributes.addFlashAttribute("erro", 
            "O arquivo enviado é muito grande. O tamanho máximo permitido é " + maxSizeFormatted + ". " +
            "Por favor, escolha um arquivo menor.");
        
        // Tentar identificar de onde veio a requisição
        String referer = "produtos";
        if (ex.getMessage() != null && ex.getMessage().contains("imagemFile")) {
            referer = "produtos";
        }
        
        return "redirect:/user/" + referer;
    }
    
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " bytes";
        } else if (size < 1024 * 1024) {
            return (size / 1024) + " KB";
        } else {
            return (size / (1024 * 1024)) + " MB";
        }
    }

    @ExceptionHandler(SessaoPagamentoInvalidaException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleSessaoPagamentoInvalidaException(SessaoPagamentoInvalidaException ex, Model model) {
        log.error("Erro de sessão de pagamento: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Sessão de pagamento inválida ou expirada. Por favor, inicie o processo de pagamento novamente.");
        model.addAttribute("errorTitle", "Sessão Expirada");
        model.addAttribute("errorCode", "401");
        return "error/payment-error";
    }

    @ExceptionHandler(PagamentoStatusNaoPendenteException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public String handlePagamentoStatusNaoPendenteException(PagamentoStatusNaoPendenteException ex, Model model) {
        log.error("Erro de status de pagamento: {}", ex.getMessage());
        model.addAttribute("title", "Intenção de Pagamento não está pendente");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("errorMessage", "Este pagamento não pode ser processado porque seu status atual não é 'Pendente'.");
        model.addAttribute("errorTitle", "Pagamento Não Processável");
        model.addAttribute("errorCode", "412");
        return "error/erro-412";
    }

    @ExceptionHandler(PagamentoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlePagamentoNaoEncontradoException(PagamentoNaoEncontradoException ex, Model model) {
        log.error("Pagamento não encontrado: {}", ex.getMessage());
        model.addAttribute("errorMessage", "O pagamento solicitado não foi encontrado.");
        model.addAttribute("errorTitle", "Pagamento Não Encontrado");
        model.addAttribute("errorCode", "404");
        return "error/payment-error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Erro interno do servidor: {}", ex.getMessage(), ex);
        model.addAttribute("errorMessage", "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.");
        model.addAttribute("errorTitle", "Erro Interno");
        model.addAttribute("errorCode", "500");
        return "error/payment-error";
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String error400(MissingServletRequestParameterException ex, Model model) {
        model.addAttribute("mensagem",  ex.getMessage());
        return "erro-400";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String error400(MethodArgumentNotValidException ex, Model model) {
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        model.addAttribute("mensagem",  errors.toString());
        return "erro-400";
    }

    @ExceptionHandler(FingerprintInvalidoException.class)
    public String error401(FingerprintInvalidoException ex, Model model) {
        model.addAttribute("title", "Fingerprint inválido");
        model.addAttribute("message",  ex.getMessage());
        return "erro-401";
    }

    @ExceptionHandler(IntencaoPagamentoNaoExisteExeption.class)
    public String error404(IntencaoPagamentoNaoExisteExeption ex, Model model) {
        model.addAttribute("title", "Intenção de Pagamento não encontrada");
        model.addAttribute("message",  ex.getMessage());
        return "erro-404";
    }


    @ExceptionHandler(Throwable.class)
    public String error500(Throwable ex) {
        ex.printStackTrace();
        return "erro-500.html";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String error503(NoResourceFoundException ex) {
        ex.printStackTrace();
        return "erro-404.html";
    }
}
