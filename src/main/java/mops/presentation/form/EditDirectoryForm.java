package mops.presentation.form;

import lombok.Data;
import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissions;
import mops.persistence.permission.DirectoryPermissionsBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper for the edit directory form for thymeleaf.
 */
@Data
public class EditDirectoryForm {

    /**
     * Directory name.
     */
    private String name;

    /**
     * Role Permissions.
     */
    private List<RolePermissionsForm> rolePermissions;

    /**
     * Creates a new EditDirectoryForm from an existing directory and permissions.
     *
     * @param directory   directory
     * @param permissions directory permissions
     * @return EditDirectoryForm
     */
    public static EditDirectoryForm of(Directory directory, DirectoryPermissions permissions) {
        EditDirectoryForm form = new EditDirectoryForm();
        form.setName(directory.getName());
        form.setRolePermissions(
                permissions.getRoles().stream()
                        .map(role -> {
                            RolePermissionsForm permissionsForm = new RolePermissionsForm();
                            permissionsForm.setRole(role);
                            permissionsForm.setRead(permissions.isAllowedToRead(role));
                            permissionsForm.setWrite(permissions.isAllowedToWrite(role));
                            permissionsForm.setDelete(permissions.isAllowedToDelete(role));
                            return permissionsForm;
                        })
                        .collect(Collectors.toList())
        );
        return form;
    }

    /**
     * Extract directory permissions object.
     *
     * @return directory permissions
     */
    public DirectoryPermissions buildDirectoryPermissions() {
        DirectoryPermissionsBuilder builder = DirectoryPermissions.builder();
        rolePermissions.forEach(rolePermissions -> builder.entry(
                rolePermissions.getRole(),
                rolePermissions.isRead(),
                rolePermissions.isWrite(),
                rolePermissions.isDelete()
                )
        );
        return builder.build();
    }
}
