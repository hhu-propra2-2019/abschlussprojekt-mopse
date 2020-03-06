package mops;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class Account {
    /**
     * Name of the user.
     */
    private final String name;
    /**
     * Email of the user.
     */
    private final String email;
    /**
     * Avatar of the user.
     */
    private final String image;
    /**
     * Keycloak roles of the user.
     */
    private final Set<String> roles;
}
