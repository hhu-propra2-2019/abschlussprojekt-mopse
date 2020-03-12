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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    static final String STUDENTIN = "studentin";
    static final String ADMIN = "admin";
    static final String EDITOR = "editor";
    static final String USER = "user";
    static final String INTRUDER = "intruder";
    static final long GROUP_ID = 0L;
    static final long PERMISSIONS_ID = 0L;
    static final long ROOT_DIR_ID = 0L;

    @Mock
    PermissionService permissionService;
    @Mock
    DirectoryPermissionsRepository directoryPermissionsRepository;

    RoleService roleService;

    Directory root;
    Account admin;
    Account editor;
    Account user;
    Account intruder;

    @BeforeEach
    void setup() {
        roleService = new RoleServiceImpl(permissionService, directoryPermissionsRepository);

        admin = Account.of(ADMIN, ADMIN + "@hhu.de", STUDENTIN);
        editor = Account.of(EDITOR, EDITOR + "@hhu.de", STUDENTIN);
        user = Account.of(USER, USER + "@hhu.de", STUDENTIN);
        intruder = Account.of(INTRUDER, INTRUDER + "@hhu.de", STUDENTIN);

        DirectoryPermissions permissions = DirectoryPermissions.builder()
                .id(PERMISSIONS_ID)
                .entry(ADMIN, true, true, true)
                .entry(EDITOR, true, true, false)
                .entry(USER, true, false, false)
                .build();
        root = Directory.builder()
                .id(ROOT_DIR_ID)
                .name("")
                .groupOwner(GROUP_ID)
                .permissions(PERMISSIONS_ID)
                .build();

        given(directoryPermissionsRepository.findById(PERMISSIONS_ID)).willReturn(Optional.of(permissions));

        given(permissionService.fetchRolesInGroup(GROUP_ID)).willReturn(Set.of(ADMIN, EDITOR, USER));
        given(permissionService.fetchRoleForUserInGroup(admin, GROUP_ID)).willReturn(ADMIN);
        given(permissionService.fetchRoleForUserInGroup(editor, GROUP_ID)).willReturn(EDITOR);
        given(permissionService.fetchRoleForUserInGroup(user, GROUP_ID)).willReturn(USER);
        given(permissionService.fetchRoleForUserInGroup(intruder, GROUP_ID)).willReturn(INTRUDER);
    }

    @Test
    void checkReadPermission() {
        assertThatCode(() -> roleService.checkReadPermission(admin, root))
                .doesNotThrowAnyException();
        assertThatCode(() -> roleService.checkReadPermission(editor, root))
                .doesNotThrowAnyException();
        assertThatCode(() -> roleService.checkReadPermission(user, root))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> roleService.checkReadPermission(intruder, root))
                .isInstanceOf(ReadAccessPermissionException.class);
    }

    @Test
    void checkWritePermission() {
        assertThatCode(() -> roleService.checkWritePermission(admin, root))
                .doesNotThrowAnyException();
        assertThatCode(() -> roleService.checkWritePermission(editor, root))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> roleService.checkWritePermission(user, root))
                .isInstanceOf(WriteAccessPermissionException.class);
        assertThatThrownBy(() -> roleService.checkWritePermission(intruder, root))
                .isInstanceOf(WriteAccessPermissionException.class);
    }

    @Test
    void checkDeletePermission() {
        assertThatCode(() -> roleService.checkDeletePermission(admin, root))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> roleService.checkDeletePermission(editor, root))
                .isInstanceOf(DeleteAccessPermissionException.class);
        assertThatThrownBy(() -> roleService.checkDeletePermission(user, root))
                .isInstanceOf(DeleteAccessPermissionException.class);
        assertThatThrownBy(() -> roleService.checkDeletePermission(intruder, root))
                .isInstanceOf(DeleteAccessPermissionException.class);
    }

    @Test
    void checkIfRole() {
        assertThatCode(() -> roleService.checkIfRole(user, GROUP_ID, USER))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> roleService.checkIfRole(user, GROUP_ID, ADMIN))
                .isInstanceOf(WriteAccessPermissionException.class);
    }
}