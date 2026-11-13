package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.service.CriptografiaService;
import cv.ntinnovation.vintipay.application.port.in.GerenciaCredenciaisConfiguracaoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class GerenciaCredenciaisConfiguracaoUseCaseImpl implements GerenciaCredenciaisConfiguracaoUseCase {

    private final BuscaConfiguracaoPagamentoPort buscaConfiguracaoPagamentoPort;
    private final SalvaConfiguracaoPagamentoPort salvaConfiguracaoPagamentoPort;
    private final CriptografiaService criptografiaService;

    @Override
    public String renovarApiSecret(Long configuracaoId, Long usuarioId) {
        ConfiguracaoPagamento configuracao = buscarConfiguracaoDoUsuario(configuracaoId, usuarioId);
        String novoApiSecret = gerarApiSecret();
        configuracao.setApiSecret(criptografiaService.criptografar(novoApiSecret));
        configuracao.setUpdatedAt(LocalDateTime.now());
        salvaConfiguracaoPagamentoPort.salvar(configuracao);
        return novoApiSecret;
    }

    @Override
    public String renovarReturnAutCode(Long configuracaoId, Long usuarioId) {
        ConfiguracaoPagamento configuracao = buscarConfiguracaoDoUsuario(configuracaoId, usuarioId);
        String novoReturnAutCode = gerarSecret(16);
        configuracao.setReturnAutCode(criptografiaService.criptografar(novoReturnAutCode));
        configuracao.setUpdatedAt(LocalDateTime.now());
        salvaConfiguracaoPagamentoPort.salvar(configuracao);
        return novoReturnAutCode;
    }

    @Override
    public String renovarWebhookSecret(Long configuracaoId, Long usuarioId) {
        ConfiguracaoPagamento configuracao = buscarConfiguracaoDoUsuario(configuracaoId, usuarioId);
        String novoWebhookSecret = gerarSecret(32);
        configuracao.setWebhookSecret(criptografiaService.criptografar(novoWebhookSecret));
        configuracao.setUpdatedAt(LocalDateTime.now());
        salvaConfiguracaoPagamentoPort.salvar(configuracao);
        return novoWebhookSecret;
    }

    private ConfiguracaoPagamento buscarConfiguracaoDoUsuario(Long configuracaoId, Long usuarioId) {
        ConfiguracaoPagamento configuracao = buscaConfiguracaoPagamentoPort.buscarPorId(configuracaoId)
                .orElseThrow(() -> new IllegalArgumentException("Configuração não encontrada"));

        if (configuracao.getUsuarioId() == null || !configuracao.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("Configuração não pertence ao usuário");
        }

        return configuracao;
    }

    private String gerarApiSecret() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private String gerarSecret(int tamanho) {
        byte[] bytes = new byte[tamanho];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
