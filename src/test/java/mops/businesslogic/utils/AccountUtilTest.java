package mops.businesslogic.utils;

import mops.businesslogic.Account;
import org.junit.jupiter.api.Test;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AccountUtilTest {

    /**
     * Tests if the account is correctly build from token.
     */
    @Test
    void getAccountFromToken() {
        String userName = "studi";
        String userEmail = "bla@bla.com";
        Set<String> roles = Set.of("studentin");

        KeycloakPrincipal<?> principal = mock(KeycloakPrincipal.class, RETURNS_DEEP_STUBS);
        when(principal.getName()).thenReturn(userName);
        when(principal.getKeycloakSecurityContext().getIdToken().getEmail()).thenReturn(userEmail);
        SimpleKeycloakAccount keycloakAccount = new SimpleKeycloakAccount(principal, roles,
                mock(RefreshableKeycloakSecurityContext.class));
        KeycloakAuthenticationToken keycloakAuthenticationToken = new KeycloakAuthenticationToken(keycloakAccount,
                true);

        Account expectedAccount = Account.of(userName, userEmail, roles);

        Account accountFromToken = AccountUtil.getAccountFromToken(keycloakAuthenticationToken);

        assertThat(accountFromToken).isEqualToComparingFieldByField(expectedAccount);
    }
}
