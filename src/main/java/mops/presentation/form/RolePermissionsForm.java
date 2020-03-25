package mops.presentation.form;

import lombok.Data;

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
