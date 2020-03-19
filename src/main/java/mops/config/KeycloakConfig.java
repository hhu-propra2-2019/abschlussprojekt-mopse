package mops.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.client.RestTemplate;


/**
 * WORKAROUND for https://issues.redhat.com/browse/KEYCLOAK-11282.
 * Bean should move into {@link SecurityConfig} once Bug has been resolved
 */

@Configuration
@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public class KeycloakConfig {
    /**
     * Keycloak ressource.
     */
    @Value("${keycloak.resource}")
    private String clientId;

    /**
     * Keycloak credentials.
     */
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    /**
     * Keycloak token URI.
     */
    @Value("${hhu_keycloak.token-uri}")
    private String tokenUri;

    /**
     * Necessary keycloak config.
     *
     * @return KeycloakSpringBootConfigResolver
     */
    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    /**
     * Creates a keycloak secured keycloak restTemplate.
     *
     * @return secured restTemplate
     */
    @Bean
    @SuppressWarnings("deprecation") // best solution as of now
    public RestTemplate serviceAccountRestTemplate() {
        ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
        resourceDetails.setGrantType(OAuth2Constants.CLIENT_CREDENTIALS);
        resourceDetails.setAccessTokenUri(tokenUri);
        resourceDetails.setClientId(clientId);
        resourceDetails.setClientSecret(clientSecret);

        return new OAuth2RestTemplate(resourceDetails);
    }

}
