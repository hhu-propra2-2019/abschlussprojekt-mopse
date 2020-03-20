package mops.businesslogic.utils;

import mops.businesslogic.Account;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

/**
 * Builds an account.
 */
public class AccountUtil {

    /**
     * Creates an Account Object from a token.
     *
     * @param token security token provided by keycloak
     * @return {@link Account}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public static Account getAccountFromToken(KeycloakAuthenticationToken token) {
        KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) token.getPrincipal();
        return Account.of(
                principal.getName(),
                principal.getKeycloakSecurityContext().getIdToken().getEmail(),
                token.getAccount().getRoles()
        );
    }
}
