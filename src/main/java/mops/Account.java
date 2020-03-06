package mops;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
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
