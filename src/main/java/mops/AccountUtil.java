package mops;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

public class AccountUtil {

    /**
     * Creates an Account Object from a token.
     *
     * @param token security token provided by keycloak
     * @return {@link Account}
     */
    @SuppressWarnings("PMD")
    public static Account getAccountFromToken(KeycloakAuthenticationToken token) {
        //noinspection rawtypes as it is convention for this type
        final KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        return new Account(
                principal.getName(),
                principal.getKeycloakSecurityContext().getIdToken().getEmail(),
                null,
                token.getAccount().getRoles());
    }
}
