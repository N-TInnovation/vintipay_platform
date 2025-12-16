package cv.ntinnovation.vintipay.adapter.out.client.api.verifika;


import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.mapper.UserRepresentationMapper;
import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.response.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class VerifikaAdapter {

    private final VerifikaClient verifikaClient;
    private final UserRepresentationMapper userRepresentationMapper;

    public VerifikaAdapter(@Lazy VerifikaClient verifikaClient, UserRepresentationMapper userRepresentationMapper) {
        this.verifikaClient = verifikaClient;
        this.userRepresentationMapper = userRepresentationMapper;
    }

    @Value("${verifika.realm}")
    private String realm;

    /**
     * Obtém o email do usuário atualmente logado via SecurityContext
     */
    public Optional<UserRepresentation> obterUsuarioLogado() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Nenhum usuário autenticado no contexto");
                return Optional.empty();
            }

            Object principal = authentication.getPrincipal();

            OAuth2User oauth2User = (OAuth2User) principal;

            return Optional.of(userRepresentationMapper.fromOAuth2User(oauth2User));

        } catch (Exception e) {
            log.error("Erro ao obter email do usuário logado: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Busca um usuário por ID
     * @param id
     */
    public Optional<UserRepresentation> buscarUsuarioPorId(String id) {
        return Optional.of(userRepresentationMapper.fromUserResponse(verifikaClient.getUser(id)));
    }

    /**
     * Busca um usuário por email
     */
    public Optional<UserRepresentation> buscarUsuarioPorEmail(String email) {

            return Optional.of(userRepresentationMapper.fromUserResponse(verifikaClient.getUserByEmail(email)));
    }

    /**
     * Atualiza atributos customizados de um usuário no Keycloak
     */
    public boolean atualizarAtributos(String id, Map<String, List<String>> atributos) {

        UserResponse userResponse = verifikaClient.getUser(id);

        UserRepresentation userLogado = userRepresentationMapper.fromUserResponse(userResponse);

        // Obter atributos atuais ou criar novo mapa
        Map<String, List<String>> atributosAtuais = userLogado.getAttributes();
        if (atributosAtuais == null) {
            atributosAtuais = new HashMap<>();
        }
        
        // Adicionar novos atributos sem duplicar valores
        for (Map.Entry<String, List<String>> entry : atributos.entrySet()) {
            String chave = entry.getKey();
            List<String> novosValores = entry.getValue();
            
            if (atributosAtuais.containsKey(chave)) {
                // Se a chave já existe, adicionar apenas valores que não estão presentes
                List<String> valoresExistentes = atributosAtuais.get(chave);
                for (String novoValor : novosValores) {
                    if (!valoresExistentes.contains(novoValor)) {
                        valoresExistentes.add(novoValor);
                    }
                }
            } else {
                // Se a chave não existe, adicionar normalmente
                atributosAtuais.put(chave, new ArrayList<>(novosValores));
            }
        }

        log.info("Usuário: {}", userLogado.getUsername());
        log.info("Atributos atuais após mesclagem: {}", atributosAtuais);
        userLogado.setAttributes(atributosAtuais);

        verifikaClient.updateUser(id, userRepresentationMapper.toUserRequest(userLogado));

        return true;
    }
}
