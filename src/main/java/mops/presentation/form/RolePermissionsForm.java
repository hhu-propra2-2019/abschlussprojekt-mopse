package mops.presentation.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Permissions form wrapper.
 */
@Data
public class RolePermissionsForm {

    /**
     * Role.
     */
    private String role;
    /**
     * Can read.
     */
    private boolean read;
    /**
     * Can write.
     */
    private boolean write;
    /**
     * Can delete.
     */
    private boolean delete;

}
