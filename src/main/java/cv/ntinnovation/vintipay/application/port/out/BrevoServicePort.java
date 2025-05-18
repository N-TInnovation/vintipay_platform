package cv.ntinnovation.vintipay.application.port.out;

public interface BrevoServicePort {
    void sendEmail(String to, String subject, String body);
}
