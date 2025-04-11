package cv.ntinnovation.gateway_payment.application.core.usecase;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.SignedJWT;
import cv.ntinnovation.gateway_payment.application.port.in.CriarAutorizacaoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;

@RequiredArgsConstructor
@Service
public class CriarAutorizacaoUseCaseImpl implements CriarAutorizacaoUseCase {
    private final JwtEncoder jwtEncoder;
    private final JWSVerifier verifier;

    @Override
    public String getToken(String issuer, String subject) {
        var expireIn = 600L;
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(subject)
                .expiresAt(now.plusSeconds(expireIn))
                .issuedAt(now)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public Boolean verify(String token) {
        SignedJWT signedJWT;
        try {
            signedJWT = SignedJWT.parse(token);
            return signedJWT.verify(verifier);
        } catch (ParseException | JOSEException e) {
            return false;
        }
    }
}
