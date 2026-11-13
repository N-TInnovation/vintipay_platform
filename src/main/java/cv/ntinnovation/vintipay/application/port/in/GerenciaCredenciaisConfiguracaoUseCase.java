package cv.ntinnovation.vintipay.application.port.in;

public interface GerenciaCredenciaisConfiguracaoUseCase {

    String renovarApiSecret(Long configuracaoId, Long usuarioId);

    String renovarReturnAutCode(Long configuracaoId, Long usuarioId);

    String renovarWebhookSecret(Long configuracaoId, Long usuarioId);
}
