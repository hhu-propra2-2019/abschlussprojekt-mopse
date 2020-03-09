package mops.businesslogic.utils;

import mops.businesslogic.Account;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

public class AccountUtil {

    /**
     * Creates an Account Object from a token.
     *
     * @param token security token provided by keycloak
     * @return {@link Account}
     */
    @SuppressWarnings({ "PMD", "rawtypes" })
    public static Account getAccountFromToken(KeycloakAuthenticationToken token) {
        KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        return new Account(
                principal.getName(),
                principal.getKeycloakSecurityContext().getIdToken().getEmail(),
                token.getAccount().getRoles());
    }
}
