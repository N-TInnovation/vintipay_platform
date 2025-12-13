package cv.ntinnovation.vintipay.adapter.out.keycloak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakAdminAdapter {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${keycloak.enabled:true}")
    private boolean keycloakEnabled;

    private Keycloak keycloak;

    @PostConstruct
    public void init() {
        if (!keycloakEnabled) {
            log.info("Keycloak está desabilitado. Pulando inicialização.");
            return;
        }

        try {
            this.keycloak = KeycloakBuilder.builder()
                    .serverUrl(authServerUrl)
                    .realm("master") // Usar realm master para admin
                    .clientId("admin-cli")
                    .username("admin")
                    .password("admin")
                    .build();
            log.info("Keycloak Admin Client inicializado com sucesso");
        } catch (Exception e) {
            log.error("Erro ao inicializar Keycloak Admin Client: {}", e.getMessage());
        }
    }

    /**
     * Cria um novo usuário no Keycloak
     */
    public Optional<String> criarUsuario(String username, String email, String nome, String password, String... roles) {
        if (!keycloakEnabled || keycloak == null) {
            log.warn("Keycloak está desabilitado ou não inicializado. Não é possível criar usuário.");
            return Optional.empty();
        }

        try {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();

            // Verificar se usuário já existe
            List<UserRepresentation> existingUsers = usersResource.search(username);
            if (!existingUsers.isEmpty()) {
                log.warn("Usuário {} já existe no Keycloak", username);
                return Optional.of(existingUsers.get(0).getId());
            }

            // Criar representação do usuário
            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(nome);
            user.setEnabled(true);
            user.setEmailVerified(false);

            // Criar usuário
            Response response = usersResource.create(user);
            
            if (response.getStatus() == 201) {
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                log.info("Usuário {} criado no Keycloak com ID: {}", username, userId);

                // Definir senha
                UserResource userResource = usersResource.get(userId);
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(password);
                credential.setTemporary(false);
                userResource.resetPassword(credential);

                // Atribuir roles
                if (roles != null && roles.length > 0) {
                    atribuirRoles(userId, roles);
                }

                response.close();
                return Optional.of(userId);
            } else {
                log.error("Erro ao criar usuário no Keycloak. Status: {}", response.getStatus());
                response.close();
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Erro ao criar usuário no Keycloak: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Atribui roles a um usuário
     */
    public void atribuirRoles(String userId, String... roleNames) {
        if (!keycloakEnabled || keycloak == null) {
            return;
        }

        try {
            RealmResource realmResource = keycloak.realm(realm);
            UserResource userResource = realmResource.users().get(userId);

            for (String roleName : roleNames) {
                RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
                userResource.roles().realmLevel().add(Collections.singletonList(role));
                log.info("Role {} atribuída ao usuário {}", roleName, userId);
            }
        } catch (Exception e) {
            log.error("Erro ao atribuir roles ao usuário: {}", e.getMessage(), e);
        }
    }

    /**
     * Atualiza a senha de um usuário
     */
    public boolean atualizarSenha(String userId, String novaSenha) {
        if (!keycloakEnabled || keycloak == null) {
            return false;
        }

        try {
            RealmResource realmResource = keycloak.realm(realm);
            UserResource userResource = realmResource.users().get(userId);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(novaSenha);
            credential.setTemporary(false);
            userResource.resetPassword(credential);

            log.info("Senha atualizada para usuário {}", userId);
            return true;
        } catch (Exception e) {
            log.error("Erro ao atualizar senha do usuário: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Busca um usuário por username
     */
    public Optional<UserRepresentation> buscarUsuarioPorUsername(String username) {
        if (!keycloakEnabled || keycloak == null) {
            return Optional.empty();
        }

        try {
            RealmResource realmResource = keycloak.realm(realm);
            List<UserRepresentation> users = realmResource.users().search(username, true);
            
            return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
        } catch (Exception e) {
            log.error("Erro ao buscar usuário no Keycloak: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Deleta um usuário do Keycloak
     */
    public boolean deletarUsuario(String userId) {
        if (!keycloakEnabled || keycloak == null) {
            return false;
        }

        try {
            RealmResource realmResource = keycloak.realm(realm);
            realmResource.users().delete(userId);
            log.info("Usuário {} deletado do Keycloak", userId);
            return true;
        } catch (Exception e) {
            log.error("Erro ao deletar usuário do Keycloak: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Verifica o email de um usuário
     */
    public boolean verificarEmail(String userId) {
        if (!keycloakEnabled || keycloak == null) {
            return false;
        }

        try {
            RealmResource realmResource = keycloak.realm(realm);
            UserResource userResource = realmResource.users().get(userId);
            UserRepresentation user = userResource.toRepresentation();
            user.setEmailVerified(true);
            userResource.update(user);
            
            log.info("Email verificado para usuário {}", userId);
            return true;
        } catch (Exception e) {
            log.error("Erro ao verificar email do usuário: {}", e.getMessage(), e);
            return false;
        }
    }
}
