package mops.businesslogic.security;

import mops.businesslogic.exception.DeleteAccessPermissionException;
import mops.businesslogic.exception.ReadAccessPermissionException;
import mops.businesslogic.exception.WriteAccessPermissionException;
import mops.businesslogic.group.GroupService;
import mops.businesslogic.permission.PermissionService;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SecurityServiceTest {

    static final String STUDENTIN = "studentin";
    static final String ADMIN = "admin";
    static final String EDITOR = "editor";
    static final String USER = "user";
    static final String INTRUDER = "intruder";
    static final long GROUP_ID = 0L;
    static final long PERMISSIONS_ID = 0L;
    static final long ROOT_DIR_ID = 0L;

    @Mock
    GroupService groupService;
    @Mock
    PermissionService permissionService;

    SecurityService securityService;

    Directory root;
    Account admin;
    Account editor;
    Account user;
    Account intruder;

    @BeforeEach
    void setup() throws MopsException {
        securityService = new SecurityServiceImpl(groupService, permissionService);

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

        given(permissionService.getPermissions(root)).willReturn(permissions);

        given(groupService.getRoles(GROUP_ID)).willReturn(Set.of(ADMIN, EDITOR, USER));
        given(groupService.getUserRole(admin, GROUP_ID)).willReturn(ADMIN);
        given(groupService.getUserRole(editor, GROUP_ID)).willReturn(EDITOR);
        given(groupService.getUserRole(user, GROUP_ID)).willReturn(USER);
        given(groupService.getUserRole(intruder, GROUP_ID)).willReturn(INTRUDER);
    }

    @Test
    void checkReadPermission() {
        assertThatCode(() -> securityService.checkReadPermission(admin, root))
                .doesNotThrowAnyException();
        assertThatCode(() -> securityService.checkReadPermission(editor, root))
                .doesNotThrowAnyException();
        assertThatCode(() -> securityService.checkReadPermission(user, root))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> securityService.checkReadPermission(intruder, root))
                .isInstanceOf(ReadAccessPermissionException.class);
    }

    @Test
    void checkWritePermission() {
        assertThatCode(() -> securityService.checkWritePermission(admin, root))
                .doesNotThrowAnyException();
        assertThatCode(() -> securityService.checkWritePermission(editor, root))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> securityService.checkWritePermission(user, root))
                .isInstanceOf(WriteAccessPermissionException.class);
        assertThatThrownBy(() -> securityService.checkWritePermission(intruder, root))
                .isInstanceOf(WriteAccessPermissionException.class);
    }

    @Test
    void checkDeletePermission() {
        assertThatCode(() -> securityService.checkDeletePermission(admin, root))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> securityService.checkDeletePermission(editor, root))
                .isInstanceOf(DeleteAccessPermissionException.class);
        assertThatThrownBy(() -> securityService.checkDeletePermission(user, root))
                .isInstanceOf(DeleteAccessPermissionException.class);
        assertThatThrownBy(() -> securityService.checkDeletePermission(intruder, root))
                .isInstanceOf(DeleteAccessPermissionException.class);
    }

    @Test
    void checkIfRole() {
        assertThatCode(() -> securityService.checkIfRole(user, GROUP_ID, USER))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> securityService.checkIfRole(user, GROUP_ID, ADMIN))
                .isInstanceOf(WriteAccessPermissionException.class);
    }
}