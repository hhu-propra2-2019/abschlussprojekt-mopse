package mops.businesslogic.security;

import mops.businesslogic.permission.PermissionService;
import mops.businesslogic.permission.PermissionServiceImpl;
import mops.exception.MopsException;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    DirectoryPermissionsRepository permissionsRepository;

    PermissionService permissionService;

    Directory directory;
    Directory otherDirectory;
    DirectoryPermissions permissions;

    @BeforeEach
    void setup() {
        permissionService = new PermissionServiceImpl(permissionsRepository);

        directory = Directory.builder()
                .id(1L)
                .name("")
                .groupOwner(100L)
                .permissions(2L)
                .build();
        otherDirectory = Directory.builder()
                .id(2L)
                .name("")
                .groupOwner(100L)
                .permissions(3L)
                .build();

        permissions = DirectoryPermissions.builder()
                .entry("admin", true, true, true)
                .build();
    }

    @Test
    void getPermissions() throws MopsException {
        given(permissionsRepository.findById(directory.getPermissionsId()))
                .willReturn(Optional.of(permissions));

        DirectoryPermissions result = permissionService.getPermissions(directory);

        assertThat(result).isEqualTo(permissions);
    }

    @Test
    void getPermissionsError() {
        given(permissionsRepository.findById(otherDirectory.getPermissionsId()))
                .willThrow(DbActionExecutionException.class);

        assertThatThrownBy(() -> permissionService.getPermissions(otherDirectory))
                .isInstanceOf(MopsException.class);
    }

    @Test
    void savePermissions() throws MopsException {
        given(permissionsRepository.save(permissions))
                .willReturn(permissions);

        DirectoryPermissions result = permissionService.savePermissions(permissions);

        assertThat(result).isEqualTo(permissions);
    }

    @Test
    void savePermissionsError() {
        given(permissionsRepository.save(permissions))
                .willThrow(DbActionExecutionException.class);

        assertThatThrownBy(() -> permissionService.savePermissions(permissions))
                .isInstanceOf(MopsException.class);
    }
}
