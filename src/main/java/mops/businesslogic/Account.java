package mops.businesslogic;

import lombok.NonNull;
import lombok.Value;

import java.util.Set;

/**
 * Wrapper for keycloak credentials.
 */
@Value(staticConstructor = "of")
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
     * @param roles permission roles
     * @return account
     */
    public static Account of(String name, String email, String... roles) {
        return new Account(name, email, null, Set.of(roles));
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
        return new Account(name, email, null, roles);
    }
}
