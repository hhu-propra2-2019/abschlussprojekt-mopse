package mops.businesslogic.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Wrapper for keycloak credentials.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
// @Value automatically makes all fields `private final` which CheckStyle and PMD don't see
@SuppressWarnings({ "checkstyle:VisibilityModifier", "PMD.DefaultPackage" })
public class Account {

    /**
     * Name of the user.
     */
    @NonNull
    String name;
    /**
     * Email of the user.
     */
    @NonNull
    String email;
    /**
     * Avatar of the user.
     */
    String image;
    /**
     * Keycloak roles of the user.
     */
    @NonNull
    Set<String> roles;

    /**
     * Create a new Account.
     *
     * @param name  user name
     * @param email email address
     * @param image profile image
     * @param roles permission roles
     * @return account
     */
    @SuppressWarnings("PMD.LawOfDemeter") // stream
    public static Account of(@NonNull String name, @NonNull String email, String image, @NonNull Set<String> roles) {
        return new Account(
                name,
                email,
                image,
                roles.stream().map(role -> role.toLowerCase(Locale.ROOT)).collect(Collectors.toUnmodifiableSet())
        );
    }

    /**
     * Create a new Account.
     *
     * @param name  user name
     * @param email email address
     * @param roles permission roles
     * @return account
     */
    public static Account of(String name, String email, String... roles) {
        return of(name, email, null, Set.of(roles));
    }

    /**
     * Create a new Account.
     *
     * @param name  user name
     * @param email email address
     * @param roles permission roles
     * @return account
     */
    public static Account of(String name, String email, Set<String> roles) {
        return of(name, email, null, roles);
    }

    /**
     * Create a new Account.
     *
     * @param token keycloak token
     * @return account
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public static Account of(KeycloakAuthenticationToken token) {
        KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) token.getPrincipal();
        return of(
                principal.getName(),
                principal.getKeycloakSecurityContext().getIdToken().getEmail(),
                token.getAccount().getRoles()
        );
    }
}
