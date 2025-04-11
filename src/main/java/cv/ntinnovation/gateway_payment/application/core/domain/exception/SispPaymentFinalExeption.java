package cv.ntinnovation.gateway_payment.application.core.domain.exception;

public class SispPaymentFinalExeption extends RuntimeException{
    public SispPaymentFinalExeption(Exception e){
        super(e);
    }
}
