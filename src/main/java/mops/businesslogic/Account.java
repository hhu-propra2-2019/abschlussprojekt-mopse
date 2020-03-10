package mops.businesslogic;

import lombok.*;

import java.util.Set;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
public class Account {
    /**
     * Name of the user.
     */
    @NonNull
    private final String name;
    /**
     * Email of the user.
     */
    @NonNull
    private final String email;
    /**
     * Keycloak roles of the user.
     */
    @NonNull
    private final Set<String> roles;
    /**
     * Avatar of the user.
     */
    private String image;

    /**
     * Create a new Account.
     *
     * @param name  user name
     * @param email email address
     * @param roles permission roles
     */
    public Account(String name, String email, String... roles) {
        this(name, email, Set.of(roles));
    }
}
