package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoIntegracoes;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaConfiguracaoPagamentoPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizaConfiguracaoPagamentoUseCaseImplTest {

    @Mock
    private SalvaConfiguracaoPagamentoPort salvaConfiguracaoPagamentoPort;

    @Mock
    private BuscaConfiguracaoPagamentoPort buscaConfiguracaoPagamentoPort;

    @Mock
    private FingerPrintService fingerPrintService;

    @InjectMocks
    private AtualizaConfiguracaoPagamentoUseCaseImpl atualizaConfiguracaoPagamentoUseCase;

    @Captor
    private ArgumentCaptor<ConfiguracaoPagamento> configuracaoCaptor;

    private ConfiguracaoPagamento configuracaoExistente;
    private ConfiguracaoPagamento configuracaoAtualizada;
    private final Long ID_CONFIGURACAO = 1L;
    private final String POS_AUT_CODE_ORIGINAL = "codigo123";
    private final String POS_AUT_CODE_CRIPTOGRAFADO = "cripto456";

    @BeforeEach
    void setUp() {
        // Configuração existente no banco
        configuracaoExistente = ConfiguracaoPagamento.builder()
                .id(ID_CONFIGURACAO)
                .posId("pos-001")
                .merchantId("merchant-001")
                .posAutCode("antigo-codigo")
                .apiSecret("secret-001")
                .returnUrl("https://old-return.com")
                .isActive(true)
                .nomeLoja("Loja Antiga")
                .urlLoja("https://old-store.com")
                .telefone("999888777")
                .email("old@store.com")
                .ambiente(AmbienteEnum.HOMOLOGACAO)
                .webhookUrl("https://old-webhook.com")
                .webhookSecret("webhook-secret-001")
                .ativo(true)
                .webhookAtivo(true)
                .returnAtivo(true)
                .observacoes("Observação antiga")
                .createdAt(LocalDateTime.now().minusDays(10))
                .updatedAt(LocalDateTime.now().minusDays(5))
                .build();

        // Configuração a ser atualizada
        configuracaoAtualizada = ConfiguracaoPagamento.builder()
                .id(ID_CONFIGURACAO)
                .posId("pos-002")
                .merchantId("merchant-002")
                .posAutCode(POS_AUT_CODE_ORIGINAL)
                .returnUrl("https://new-return.com")
                .isActive(true)
                .nomeLoja("Loja Nova")
                .urlLoja("https://new-store.com")
                .telefone("999000111")
                .email("new@store.com")
                .ambiente(AmbienteEnum.PRODUCAO)
                .webhookUrl("https://new-webhook.com")
                .webhookSecret("webhook-secret-002")
                .ativo(true)
                .webhookAtivo(false)
                .returnAtivo(true)
                .observacoes("Observação nova")
                .build();

    }

    @Test
    void atualizar_QuandoConfiguracaoExiste_DeveAtualizarCorretamente() {
        // Configurar comportamento padrão do fingerPrintService
        when(fingerPrintService.encryptAES256(POS_AUT_CODE_ORIGINAL)).thenReturn(POS_AUT_CODE_CRIPTOGRAFADO);
        // Arrange
        when(buscaConfiguracaoPagamentoPort.buscarPorId(ID_CONFIGURACAO)).thenReturn(Optional.of(configuracaoExistente));
        when(salvaConfiguracaoPagamentoPort.salvar(any(ConfiguracaoPagamento.class))).thenReturn(configuracaoExistente);

        // Act
        ConfiguracaoPagamento resultado = atualizaConfiguracaoPagamentoUseCase.atualizar(configuracaoAtualizada);

        // Assert
        verify(buscaConfiguracaoPagamentoPort).buscarPorId(ID_CONFIGURACAO);
        verify(fingerPrintService).encryptAES256(POS_AUT_CODE_ORIGINAL);
        verify(salvaConfiguracaoPagamentoPort).salvar(configuracaoCaptor.capture());

        ConfiguracaoPagamento configuracaoSalva = configuracaoCaptor.getValue();
        assertNotNull(configuracaoSalva);
        assertEquals(ID_CONFIGURACAO, configuracaoSalva.getId());
        assertEquals("pos-002", configuracaoSalva.getPosId());
        assertEquals("merchant-002", configuracaoSalva.getMerchantId());
        assertEquals(POS_AUT_CODE_ORIGINAL, configuracaoSalva.getPosAutCode());
        assertEquals("https://new-return.com", configuracaoSalva.getReturnUrl());
        assertEquals("Loja Nova", configuracaoSalva.getNomeLoja());
        assertEquals("https://new-store.com", configuracaoSalva.getUrlLoja());
        assertEquals("999000111", configuracaoSalva.getTelefone());
        assertEquals("new@store.com", configuracaoSalva.getEmail());
        assertEquals(AmbienteEnum.PRODUCAO, configuracaoSalva.getAmbiente());
        assertEquals("https://new-webhook.com", configuracaoSalva.getWebhookUrl());
        assertEquals("webhook-secret-002", configuracaoSalva.getWebhookSecret());
        assertFalse(configuracaoSalva.getWebhookAtivo());
        assertTrue(configuracaoSalva.getReturnAtivo());
        assertEquals("Observação nova", configuracaoSalva.getObservacoes());
        assertEquals("secret-001", configuracaoSalva.getApiSecret()); // Não deve mudar se não fornecido
        assertNotNull(configuracaoSalva.getUpdatedAt());

        // Verificar que o resultado retorna o posAutCode original (não criptografado)
        assertEquals(POS_AUT_CODE_ORIGINAL, resultado.getPosAutCode());
    }

    @Test
    void atualizar_QuandoConfiguracoesAplicacoesForemFornecidas_DeveAtualizarConfiguracoes() {
        // Arrange
        List<ConfiguracaoIntegracoes> configuracoes = new ArrayList<>();
        ConfiguracaoIntegracoes app1 = ConfiguracaoIntegracoes.builder()
                .id(1L)
                .description("App 1")
                .build();
        ConfiguracaoIntegracoes app2 = ConfiguracaoIntegracoes.builder()
                .id(2L)
                .description("App 2")
                .configuracaoPagamento(configuracaoAtualizada) // Este já está configurado
                .build();
        configuracoes.add(app1);
        configuracoes.add(app2);
        
        configuracaoAtualizada.setConfiguracaoAplicacoes(configuracoes);

        // Configurar comportamento padrão do fingerPrintService
        when(fingerPrintService.encryptAES256(POS_AUT_CODE_ORIGINAL)).thenReturn(POS_AUT_CODE_CRIPTOGRAFADO);


        when(buscaConfiguracaoPagamentoPort.buscarPorId(ID_CONFIGURACAO)).thenReturn(Optional.of(configuracaoExistente));
        when(salvaConfiguracaoPagamentoPort.salvar(any(ConfiguracaoPagamento.class))).thenReturn(configuracaoExistente);

        // Act
        atualizaConfiguracaoPagamentoUseCase.atualizar(configuracaoAtualizada);

        // Assert
        verify(salvaConfiguracaoPagamentoPort).salvar(configuracaoCaptor.capture());
        ConfiguracaoPagamento configuracaoSalva = configuracaoCaptor.getValue();

        assertEquals(2, configuracaoSalva.getConfiguracaoAplicacoes().size());

    }

    @Test
    void atualizar_QuandoNovoApiSecretForFornecido_DeveAtualizarApiSecret() {
        // Arrange
        configuracaoAtualizada.setApiSecret("novo-secret-002");
        
        when(buscaConfiguracaoPagamentoPort.buscarPorId(ID_CONFIGURACAO)).thenReturn(Optional.of(configuracaoExistente));
        when(salvaConfiguracaoPagamentoPort.salvar(any(ConfiguracaoPagamento.class))).thenReturn(configuracaoExistente);

        // Act
        atualizaConfiguracaoPagamentoUseCase.atualizar(configuracaoAtualizada);

        // Assert
        verify(salvaConfiguracaoPagamentoPort).salvar(configuracaoCaptor.capture());
        ConfiguracaoPagamento configuracaoSalva = configuracaoCaptor.getValue();
        
        assertEquals("novo-secret-002", configuracaoSalva.getApiSecret());
    }

    @Test
    void atualizar_QuandoApiSecretForVazio_NaoDeveAtualizarApiSecret() {
        // Arrange
        configuracaoAtualizada.setApiSecret("");
        
        when(buscaConfiguracaoPagamentoPort.buscarPorId(ID_CONFIGURACAO)).thenReturn(Optional.of(configuracaoExistente));
        when(salvaConfiguracaoPagamentoPort.salvar(any(ConfiguracaoPagamento.class))).thenReturn(configuracaoExistente);

        // Act
        atualizaConfiguracaoPagamentoUseCase.atualizar(configuracaoAtualizada);

        // Assert
        verify(salvaConfiguracaoPagamentoPort).salvar(configuracaoCaptor.capture());
        ConfiguracaoPagamento configuracaoSalva = configuracaoCaptor.getValue();
        
        assertEquals("secret-001", configuracaoSalva.getApiSecret());
    }

    @Test
    void atualizar_QuandoConfiguracaoNaoExiste_DeveLancarExcecao() {
        // Arrange
        when(buscaConfiguracaoPagamentoPort.buscarPorId(ID_CONFIGURACAO)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            atualizaConfiguracaoPagamentoUseCase.atualizar(configuracaoAtualizada);
        });
        
        verify(buscaConfiguracaoPagamentoPort).buscarPorId(ID_CONFIGURACAO);
        verify(salvaConfiguracaoPagamentoPort, never()).salvar(any());
    }

    @Test
    void atualizar_QuandoCreatedAtForNull_DeveSetarDataAtual() {
        // Arrange
        configuracaoExistente.setCreatedAt(null);
        
        when(buscaConfiguracaoPagamentoPort.buscarPorId(ID_CONFIGURACAO)).thenReturn(Optional.of(configuracaoExistente));
        when(salvaConfiguracaoPagamentoPort.salvar(any(ConfiguracaoPagamento.class))).thenReturn(configuracaoExistente);

        // Act
        atualizaConfiguracaoPagamentoUseCase.atualizar(configuracaoAtualizada);

        // Assert
        verify(salvaConfiguracaoPagamentoPort).salvar(configuracaoCaptor.capture());
        ConfiguracaoPagamento configuracaoSalva = configuracaoCaptor.getValue();
        
        assertNotNull(configuracaoSalva.getCreatedAt());
    }

    @Test
    void ativar_QuandoConfiguracaoExiste_DeveAtivarConfiguracao() {
        // Arrange
        configuracaoExistente.setIsActive(false);
        when(buscaConfiguracaoPagamentoPort.buscarPorId(ID_CONFIGURACAO)).thenReturn(Optional.of(configuracaoExistente));

        // Act
        atualizaConfiguracaoPagamentoUseCase.ativar(ID_CONFIGURACAO);

        // Assert
        verify(buscaConfiguracaoPagamentoPort).buscarPorId(ID_CONFIGURACAO);
        verify(salvaConfiguracaoPagamentoPort).salvar(configuracaoCaptor.capture());

        ConfiguracaoPagamento configuracaoSalva = configuracaoCaptor.getValue();
        assertTrue(configuracaoSalva.getIsActive());
        assertNotNull(configuracaoSalva.getUpdatedAt());
    }

    @Test
    void ativar_QuandoConfiguracaoNaoExiste_DeveLancarExcecao() {
        // Arrange
        when(buscaConfiguracaoPagamentoPort.buscarPorId(ID_CONFIGURACAO)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            atualizaConfiguracaoPagamentoUseCase.ativar(ID_CONFIGURACAO);
        });
        
        verify(buscaConfiguracaoPagamentoPort).buscarPorId(ID_CONFIGURACAO);
        verify(salvaConfiguracaoPagamentoPort, never()).salvar(any());
    }

    @Test
    void desativar_QuandoConfiguracaoExiste_DeveDesativarConfiguracao() {
        // Arrange
        configuracaoExistente.setIsActive(true);
        when(buscaConfiguracaoPagamentoPort.buscarPorId(ID_CONFIGURACAO)).thenReturn(Optional.of(configuracaoExistente));

        // Act
        atualizaConfiguracaoPagamentoUseCase.desativar(ID_CONFIGURACAO);

        // Assert
        verify(buscaConfiguracaoPagamentoPort).buscarPorId(ID_CONFIGURACAO);
        verify(salvaConfiguracaoPagamentoPort).salvar(configuracaoCaptor.capture());

        ConfiguracaoPagamento configuracaoSalva = configuracaoCaptor.getValue();
        assertFalse(configuracaoSalva.getIsActive());
        assertNotNull(configuracaoSalva.getUpdatedAt());
    }

    @Test
    void desativar_QuandoConfiguracaoNaoExiste_DeveLancarExcecao() {
        // Arrange
        when(buscaConfiguracaoPagamentoPort.buscarPorId(ID_CONFIGURACAO)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            atualizaConfiguracaoPagamentoUseCase.desativar(ID_CONFIGURACAO);
        });
        
        verify(buscaConfiguracaoPagamentoPort).buscarPorId(ID_CONFIGURACAO);
        verify(salvaConfiguracaoPagamentoPort, never()).salvar(any());
    }
}
