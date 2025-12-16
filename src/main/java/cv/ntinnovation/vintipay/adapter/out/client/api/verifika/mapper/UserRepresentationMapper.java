package cv.ntinnovation.vintipay.adapter.out.client.api.verifika.mapper;

import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.request.UserRequest;
import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.response.UserResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserRepresentationMapper {

    public UserRepresentation fromOAuth2User(OAuth2User oauth2User) {
        if (oauth2User == null) {
            return null;
        }

        UserRepresentation user = new UserRepresentation();
        
        user.setEmail(oauth2User.getAttribute("email"));
        user.setUsername(oauth2User.getAttribute("preferred_username"));
        user.setFirstName(oauth2User.getAttribute("given_name"));
        user.setLastName(oauth2User.getAttribute("family_name"));
        user.setEmailVerified(oauth2User.getAttribute("email_verified"));
        user.setEnabled(true);
        
        String sub = oauth2User.getAttribute("sub");
        if (sub != null) {
            user.setId(sub);
        }

        Map<String, List<String>> attributes = new HashMap<>();
        oauth2User.getAttributes().forEach((key, value) -> {
            if (value instanceof List) {
                attributes.put(key, (List<String>) value);
            } else if (value != null) {
                attributes.put(key, List.of(value.toString()));
            }
        });
        user.setAttributes(attributes);

        return user;
    }

    public UserRepresentation fromAttributes(Map<String, Object> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return null;
        }

        UserRepresentation user = new UserRepresentation();
        
        user.setEmail(getAttributeAsString(attributes, "email"));
        user.setUsername(getAttributeAsString(attributes, "preferred_username"));
        user.setFirstName(getAttributeAsString(attributes, "given_name"));
        user.setLastName(getAttributeAsString(attributes, "family_name"));
        user.setEmailVerified(getAttributeAsBoolean(attributes, "email_verified"));
        user.setId(getAttributeAsString(attributes, "sub"));
        user.setEnabled(true);

        Map<String, List<String>> userAttributes = new HashMap<>();
        attributes.forEach((key, value) -> {
            if (value instanceof List) {
                userAttributes.put(key, (List<String>) value);
            } else if (value != null) {
                userAttributes.put(key, List.of(value.toString()));
            }
        });
        user.setAttributes(userAttributes);

        return user;
    }

    private String getAttributeAsString(Map<String, Object> attributes, String key) {
        Object value = attributes.get(key);
        return value != null ? value.toString() : null;
    }

    private Boolean getAttributeAsBoolean(Map<String, Object> attributes, String key) {
        Object value = attributes.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }

    public UserRepresentation fromUserResponse(UserResponse userResponse) {
        if (userResponse == null) {
            return null;
        }

        UserRepresentation user = new UserRepresentation();
        
        user.setId(userResponse.getId());
        user.setUsername(userResponse.getUsername());
        user.setFirstName(userResponse.getFirstName());
        user.setLastName(userResponse.getLastName());
        user.setEmail(userResponse.getEmail());
        user.setEmailVerified(userResponse.getEmailVerified());
        user.setEnabled(userResponse.getEnabled());
        user.setCreatedTimestamp(userResponse.getCreatedTimestamp());
        user.setTotp(userResponse.getTotp());
        user.setFederationLink(userResponse.getFederationLink());
        user.setServiceAccountClientId(userResponse.getServiceAccountClientId());
        user.setNotBefore(userResponse.getNotBefore());
        user.setAttributes(userResponse.getAttributes());
        user.setRequiredActions(userResponse.getRequiredActions());
        user.setRealmRoles(userResponse.getRealmRoles());
        user.setClientRoles(userResponse.getClientRoles());
        user.setGroups(userResponse.getGroups());
        user.setDisableableCredentialTypes(userResponse.getDisableableCredentialTypes());
        user.setAccess(userResponse.getAccess());
        user.setOrigin(userResponse.getOrigin());
        user.setSelf(userResponse.getSelf());

        return user;
    }

    public UserRequest toUserRequest(UserRepresentation userRepresentation) {
        if (userRepresentation == null) {
            return null;
        }

        UserRequest userRequest = new UserRequest();
        
        userRequest.setId(userRepresentation.getId());
        userRequest.setUsername(userRepresentation.getUsername());
        userRequest.setFirstName(userRepresentation.getFirstName());
        userRequest.setLastName(userRepresentation.getLastName());
        userRequest.setEmail(userRepresentation.getEmail());
        userRequest.setEmailVerified(userRepresentation.isEmailVerified());
        userRequest.setEnabled(userRepresentation.isEnabled());
        userRequest.setCreatedTimestamp(userRepresentation.getCreatedTimestamp());
        userRequest.setTotp(userRepresentation.isTotp());
        userRequest.setFederationLink(userRepresentation.getFederationLink());
        userRequest.setServiceAccountClientId(userRepresentation.getServiceAccountClientId());
        userRequest.setNotBefore(userRepresentation.getNotBefore());
        userRequest.setAttributes(userRepresentation.getAttributes());
        userRequest.setRequiredActions(userRepresentation.getRequiredActions());
        userRequest.setRealmRoles(userRepresentation.getRealmRoles());
        userRequest.setClientRoles(userRepresentation.getClientRoles());
        userRequest.setGroups(userRepresentation.getGroups());
        userRequest.setDisableableCredentialTypes(userRepresentation.getDisableableCredentialTypes());
        userRequest.setAccess(userRepresentation.getAccess());
        userRequest.setOrigin(userRepresentation.getOrigin());
        userRequest.setSelf(userRepresentation.getSelf());

        return userRequest;
    }
}
