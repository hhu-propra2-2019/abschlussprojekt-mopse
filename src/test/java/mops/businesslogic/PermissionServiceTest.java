package mops.businesslogic;

import mops.exception.MopsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

import static mops.businesslogic.PermissionServiceProdImpl.URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceTest {

    private PermissionService permissionService;

    @Mock
    private RestTemplate restTemplate;
    private long groupId;
    private String userName;

    @BeforeEach
    void setup() {
        groupId = 1L;
        restTemplate = mock(RestTemplate.class);
        permissionService = new PermissionServiceProdImpl(restTemplate);
        userName = "Carlo";

    }

    @Test
    public void fetchRoleInGroupTest() throws MopsException {
        Set<PermissionServiceProdImpl.GroupPermission> groups = Set.of(new PermissionServiceProdImpl.GroupPermission(groupId, "admin"));
        PermissionServiceProdImpl.Permission permission = new PermissionServiceProdImpl.Permission(userName, groups);
        Account carlo = Account.of(userName, "carlo@hhu.de", "admin");

        when(restTemplate.getForObject(URL, PermissionServiceProdImpl.Permission.class)).thenReturn(permission);

        String userRole = permissionService.fetchRoleForUserInGroup(carlo, groupId);

        assertThat(userRole).isEqualTo("admin");
    }

    @Test
    public void fetchRolesInGroupTest() throws MopsException {
        PermissionServiceProdImpl.GroupPermission[] roles = { new PermissionServiceProdImpl.GroupPermission(groupId, "admin"), new PermissionServiceProdImpl.GroupPermission(groupId, "editor") };
        when(restTemplate.getForObject(URL, PermissionServiceProdImpl.GroupPermission[].class)).thenReturn(roles);

        Set<String> rolesInGroup = permissionService.fetchRolesInGroup(groupId);

        assertThat(rolesInGroup).containsExactlyInAnyOrder("admin", "editor");
    }

    @Test
    public void fetchRoleExceptionThrownTest() {
        Account carlo = Account.of(userName, "carlo@hhu.de", "admin");
        when(restTemplate.getForObject(URL, PermissionServiceProdImpl.Permission.class)).thenReturn(null);

        assertThatExceptionOfType(GruppenFindungException.class).isThrownBy(() -> permissionService.fetchRoleForUserInGroup(carlo, groupId));
    }

    @Test
    public void fetchRolesExceptionThrownTest() {
        when(restTemplate.getForObject(URL, PermissionServiceProdImpl.GroupPermission[].class)).thenReturn(null);
        assertThatExceptionOfType(GruppenFindungException.class).isThrownBy(() -> permissionService.fetchRolesInGroup(groupId));
    }
}
