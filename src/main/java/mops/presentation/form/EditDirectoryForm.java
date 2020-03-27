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
     * Role Permissions.
     */
    private List<RolePermissionsForm> rolePermissions;

    /**
     * Creates a new EditDirectoryForm from an existing directory and permissions.
     *
     * @param directory   existing directory
     * @param permissions existing directory permissions
     * @return pre-filled EditDirectoryForm from given directory and permissions
     */
    @SuppressWarnings("PMD.LawOfDemeter") // streams & builder
    public static EditDirectoryForm of(DirectoryPermissions permissions) {
        EditDirectoryForm form = new EditDirectoryForm();
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
     * Extract the directory permissions object from the internal rolePermissions list.
     *
     * @return extracted directory permissions
     */
    @SuppressWarnings("PMD.LawOfDemeter") // streams & builder
    public DirectoryPermissions buildDirectoryPermissions() {
        DirectoryPermissionsBuilder builder = DirectoryPermissions.builder();
        if (rolePermissions != null) {
            rolePermissions.forEach(rolePermissions ->
                    builder.entry(
                            rolePermissions.getRole(),
                            rolePermissions.isRead(),
                            rolePermissions.isWrite(),
                            rolePermissions.isDelete()
                    )
            );
        }
        return builder.build();
    }
}
