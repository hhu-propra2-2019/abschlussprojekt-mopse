package mops.persistence.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * Represents a user.
 */
@Data
@AllArgsConstructor
class GroupMember {

    /**
     * User name.
     */
    @NonNull
    private String name;
    /**
     * User role.
     */
    @NonNull
    private String role;

}
