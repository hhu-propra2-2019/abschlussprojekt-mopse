package mops.presentation.utils;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

import static org.mockito.Mockito.*;

@SuppressWarnings("PMD")
public class SecurityContextUtil {
    /**
     * Builds a security context using mock for keycloak.
     *
     * @param userName  name of the user
     * @param userEmail email of the user
     * @param roles     roles of the user
     */
    public static void setupSecurityContextMock(String userName, String userEmail, Set<String> roles) {
        //noinspection rawtypes as is it convention for this principal
        KeycloakPrincipal principal = mock(KeycloakPrincipal.class,
                RETURNS_DEEP_STUBS);
        when(principal.getName()).thenReturn(userName);
        when(principal.getKeycloakSecurityContext().getIdToken().getEmail()).thenReturn(userEmail);
        SimpleKeycloakAccount account = new SimpleKeycloakAccount(principal,
                roles,
                mock(RefreshableKeycloakSecurityContext.class));
        KeycloakAuthenticationToken authenticationToken = new KeycloakAuthenticationToken(account, true);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authenticationToken);
    }
}
