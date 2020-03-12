package mops.businesslogic;

import mops.businesslogic.exception.DeleteAccessPermissionException;
import mops.businesslogic.exception.ReadAccessPermissionException;
import mops.businesslogic.exception.WriteAccessPermissionException;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    public static final long GROUP_ID = 1L;
    @Mock
    PermissionService permissionService;
    @Mock
    DirectoryPermissionsRepository directoryPermissionsRepository;
    private RoleServiceImpl roleService;
    private Account account;
    private Directory root;
    private Account admin;
    private Account editor;
    private Account intruder;


    @BeforeEach
    void setUp() {
        roleService = new RoleServiceImpl(permissionService, directoryPermissionsRepository);
        account = Account.of("user", "user@hhu.de", "studentin");
        admin = Account.of("admin", "user@hhu.de", "admin");
        editor = Account.of("editor", "user@hhu.de", "editor");
        intruder = Account.of("intruder", "user@hhu.de", "intruder");

        DirectoryPermissionEntry userRole = new DirectoryPermissionEntry("user", true, false, false);
        DirectoryPermissionEntry adminRole = new DirectoryPermissionEntry("admin", true, true, true);
        DirectoryPermissionEntry editorRole = new DirectoryPermissionEntry("editor", true, true, false);
        Set<DirectoryPermissionEntry> permissionEntries = Set.of(userRole, adminRole, editorRole);
        DirectoryPermissions permissions = mock(DirectoryPermissions.class);
        root = mock(Directory.class);

        lenient().when(root.getId()).thenReturn(GROUP_ID);

        lenient().when(permissions.getId()).thenReturn(1L);
        lenient().when(permissions.getPermissions()).thenReturn(permissionEntries);
        lenient().when(permissions.isAllowedToWrite("user")).thenReturn(false);
        lenient().when(permissions.isAllowedToWrite("editor")).thenReturn(true);
        lenient().when(permissions.isAllowedToRead("editor")).thenReturn(true);
        lenient().when(permissions.isAllowedToDelete("admin")).thenReturn(true);
        lenient().when(permissions.isAllowedToDelete("intruder")).thenReturn(true);

        lenient().when(directoryPermissionsRepository.findById(anyLong())).thenReturn(Optional.of(permissions));

        lenient().when(permissionService.fetchRoleForUserInDirectory(account, root)).thenReturn("user");
        lenient().when(permissionService.fetchRoleForUserInGroup(account, GROUP_ID)).thenReturn("user");
        lenient().when(permissionService.fetchRoleForUserInDirectory(editor, root)).thenReturn("editor");
        lenient().when(permissionService.fetchRoleForUserInDirectory(admin, root)).thenReturn("admin");
    }

    @Test
    public void checkWritePermission() {
        assertThatCode(() -> roleService.checkWritePermission(editor, root)).doesNotThrowAnyException();
    }

    @Test
    public void checkWritePermissionWithOutPermission() {
        assertThatExceptionOfType(WriteAccessPermissionException.class).isThrownBy(() -> roleService.checkWritePermission(account, root));
    }

    @Test
    public void checkReadPermission() {
        assertThatCode(() -> roleService.checkReadPermission(editor, root)).doesNotThrowAnyException();
    }

    @Test
    public void checkReadPermissionWithOutPermission() {
        assertThatExceptionOfType(ReadAccessPermissionException.class).isThrownBy(() -> roleService.checkReadPermission(intruder, root));
    }

    @Test
    public void checkDeletePermission() {
        assertThatCode(() -> roleService.checkDeletePermission(admin, root)).doesNotThrowAnyException();
    }

    @Test
    public void checkDeletePermissionWithOutPermission() {
        assertThatExceptionOfType(DeleteAccessPermissionException.class).isThrownBy(() -> roleService.checkDeletePermission(account, root));
    }

    @Test
    public void checkIfRole() {
        assertThatCode(() -> roleService.checkIfRole(account, GROUP_ID, "user")).doesNotThrowAnyException();
    }

    @Test
    public void checkIfRoleIsNot() {
        assertThatExceptionOfType(WriteAccessPermissionException.class).isThrownBy(() -> roleService.checkIfRole(intruder, GROUP_ID, "user"));
    }
}