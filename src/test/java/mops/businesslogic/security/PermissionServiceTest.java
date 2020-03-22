package mops.businesslogic.security;

import mops.businesslogic.exception.GruppenFindungException;
import mops.exception.MopsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    RestTemplate restTemplate;

    PermissionService permissionService;

    long groupId;
    String userName;
    Account carlo;

    @BeforeEach
    void setup() {
        groupId = 1L;
        restTemplate = mock(RestTemplate.class);
        permissionService = new PermissionServiceProdImpl(restTemplate);
        userName = "Carlo";
        carlo = Account.of(userName, "carlo@hhu.de", "admin");
    }

    @Test
    void fetchRoleInGroupTest() throws MopsException {
        Set<PermissionServiceProdImpl.GroupPermission> groups = Set.of(
                new PermissionServiceProdImpl.GroupPermission(groupId, "admin")
        );
        PermissionServiceProdImpl.Permission permission = new PermissionServiceProdImpl.Permission(userName, groups);

        when(restTemplate.getForObject(endsWith("/get-permission"), eq(PermissionServiceProdImpl.Permission.class)))
                .thenReturn(permission);

        String userRole = permissionService.fetchRoleForUserInGroup(carlo, groupId);

        assertThat(userRole).isEqualTo("admin");
    }

    @Test
    void fetchRolesInGroupTest() throws MopsException {
        PermissionServiceProdImpl.GroupPermission[] roles = {
                new PermissionServiceProdImpl.GroupPermission(groupId, "admin"),
                new PermissionServiceProdImpl.GroupPermission(groupId, "editor")
        };
        when(restTemplate.getForObject(endsWith("/get-roles"), eq(PermissionServiceProdImpl.GroupPermission[].class)))
                .thenReturn(roles);

        Set<String> rolesInGroup = permissionService.fetchRolesInGroup(groupId);

        assertThat(rolesInGroup).containsExactlyInAnyOrder("admin", "editor");
    }

    @Test
    void fetchRoleExceptionThrownTest() {
        when(restTemplate.getForObject(endsWith("/get-permission"), eq(PermissionServiceProdImpl.Permission.class)))
                .thenReturn(null);

        assertThatExceptionOfType(GruppenFindungException.class)
                .isThrownBy(() -> permissionService.fetchRoleForUserInGroup(carlo, groupId));
    }

    @Test
    void fetchRolesExceptionThrownTest() {
        when(restTemplate.getForObject(endsWith("/get-roles"), eq(PermissionServiceProdImpl.GroupPermission[].class)))
                .thenReturn(null);
        assertThatExceptionOfType(GruppenFindungException.class)
                .isThrownBy(() -> permissionService.fetchRolesInGroup(groupId));
    }
}
