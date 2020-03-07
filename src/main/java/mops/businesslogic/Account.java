package mops.businesslogic;

import lombok.*;

import java.util.Set;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
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
     * Avatar of the user.
     */
    private String image;
    /**
     * Keycloak roles of the user.
     */
    @NonNull
    private final Set<String> roles;
}
