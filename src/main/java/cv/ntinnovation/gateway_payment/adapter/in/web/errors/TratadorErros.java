package cv.ntinnovation.gateway_payment.adapter.in.web.errors;

import cv.ntinnovation.gateway_payment.application.core.domain.exception.IntencaoPagamentoNaoExisteExeption;
import cv.ntinnovation.gateway_payment.application.core.domain.exception.PagamentoStatusNaoPendenteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class TratadorErros {

    @ExceptionHandler(IntencaoPagamentoNaoExisteExeption.class)
    public String error404(IntencaoPagamentoNaoExisteExeption ex, Model model) {
        model.addAttribute("title", "Intenção de Pagamento não encontrada");
        model.addAttribute("message",  ex.getMessage());
        return "erro-404";
    }


    @ExceptionHandler(PagamentoStatusNaoPendenteException.class)
    public String error404(PagamentoStatusNaoPendenteException ex, Model model) {
        model.addAttribute("title", "Intenção de Pagamento não está pendente");
        model.addAttribute("message",  ex.getMessage());
        return "error/erro-412";
    }


    public record Messagem(Boolean success){

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
