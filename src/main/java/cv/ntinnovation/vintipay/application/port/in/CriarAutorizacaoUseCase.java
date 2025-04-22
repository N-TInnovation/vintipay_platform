package cv.ntinnovation.vintipay.application.port.in;

public interface CriarAutorizacaoUseCase {
    public String getToken(String issuer, String subject);
    public Boolean verify(String token);
}
