package mops.presentation.form;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Wrapper for the edit directory form for thymeleaf.
 */
@Data
@NoArgsConstructor
public class EditDirectoryForm {

    /**
     * Directory name.
     */
    private String name;

    /**
     * Role Permissions.
     */
    private Map<String, PermissionsForm> rolePermissions;

}
