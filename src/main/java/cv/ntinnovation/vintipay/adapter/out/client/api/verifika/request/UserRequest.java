package cv.ntinnovation.vintipay.adapter.out.client.api.verifika.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequest {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean emailVerified;
    private Map<String, List<String>> attributes;
    private Object userProfileMetadata;
    private Boolean enabled;
    private String self;
    private String origin;
    private Long createdTimestamp;
    private Boolean totp;
    private String federationLink;
    private String serviceAccountClientId;
    private List<Object> credentials;
    private Set<String> disableableCredentialTypes;
    private List<String> requiredActions;
    private List<Object> federatedIdentities;
    private List<String> realmRoles;
    private Map<String, List<String>> clientRoles;
    private List<Object> clientConsents;
    private Integer notBefore;
    private Map<String, List<String>> applicationRoles;
    private List<Object> socialLinks;
    private List<String> groups;
    private Map<String, Boolean> access;
}
