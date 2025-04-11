package cv.ntinnovation.gateway_payment.application.core.domain.exception;

public class AutorizacaoExeption extends RuntimeException{
    public AutorizacaoExeption(Exception e){
        super(e);
    }
}
