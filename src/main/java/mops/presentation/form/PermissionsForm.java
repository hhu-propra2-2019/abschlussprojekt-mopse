package mops.presentation.form;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Permissions form wrapper.
 */
@Data
@NoArgsConstructor
public class PermissionsForm {

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
