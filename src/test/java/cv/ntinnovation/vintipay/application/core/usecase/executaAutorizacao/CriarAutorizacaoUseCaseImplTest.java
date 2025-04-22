package cv.ntinnovation.vintipay.application.core.usecase.executaAutorizacao;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.SignedJWT;
import cv.ntinnovation.vintipay.application.core.usecase.CriarAutorizacaoUseCaseImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarAutorizacaoUseCaseImplTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JWSVerifier verifier;

    @InjectMocks
    private CriarAutorizacaoUseCaseImpl executaAutorizacaoUseCase;

    @Test
    void getToken_DeveRetornarTokenValido() {
        // Arrange
        String expectedToken = "jwt.token.test";
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn(expectedToken);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act
        String token = executaAutorizacaoUseCase.getToken("issuer", "subject");

        // Assert
        assertNotNull(token);
        assertEquals(expectedToken, token);
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void verify_QuandoTokenValido_DeveRetornarTrue() throws Exception {
        // Arrange
        String token = "valid.jwt.token";
        SignedJWT signedJWT = mock(SignedJWT.class);
        
        try (MockedStatic<SignedJWT> mockedStatic = mockStatic(SignedJWT.class)) {
            mockedStatic.when(() -> SignedJWT.parse(anyString())).thenReturn(signedJWT);
            when(signedJWT.verify(verifier)).thenReturn(true);

            // Act
            Boolean result = executaAutorizacaoUseCase.verify(token);

            // Assert
            assertTrue(result);
        }
    }

    @Test
    void verify_QuandoTokenInvalido_DeveRetornarFalse() throws Exception {
        // Arrange
        String token = "invalid.jwt.token";
        SignedJWT signedJWT = mock(SignedJWT.class);
        
        try (MockedStatic<SignedJWT> mockedStatic = mockStatic(SignedJWT.class)) {
            mockedStatic.when(() -> SignedJWT.parse(anyString())).thenReturn(signedJWT);
            when(signedJWT.verify(verifier)).thenReturn(false);

            // Act
            Boolean result = executaAutorizacaoUseCase.verify(token);

            // Assert
            assertFalse(result);
        }
    }

    @Test
    void verify_QuandoTokenMalFormatado_DeveRetornarFalse() {
        // Arrange
        String invalidToken = "invalid-token-format";

        // Act
        Boolean result = executaAutorizacaoUseCase.verify(invalidToken);

        // Assert
        assertFalse(result);
    }
}
