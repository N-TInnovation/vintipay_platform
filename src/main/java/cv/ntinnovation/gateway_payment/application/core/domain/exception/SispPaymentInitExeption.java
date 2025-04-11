package cv.ntinnovation.gateway_payment.application.core.domain.exception;

public class SispPaymentInitExeption extends RuntimeException{
    public SispPaymentInitExeption(Exception e){
        super(e);
    }
}
