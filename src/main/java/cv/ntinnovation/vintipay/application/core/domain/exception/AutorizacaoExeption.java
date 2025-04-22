package cv.ntinnovation.vintipay.application.core.domain.exception;

public class AutorizacaoExeption extends RuntimeException{
    public AutorizacaoExeption(Exception e){
        super(e);
    }
}
