package mops.businesslogic;

import mops.exception.MopsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

import static mops.businesslogic.PermissionServiceImpl.URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PermissionServiceTest {

    private PermissionService permissionService;

    @Mock
    private RestTemplate restTemplate;
    private long groupId;

    @BeforeEach
    void setUp() {
        groupId = 1L;
        restTemplate = mock(RestTemplate.class);
        permissionService = new PermissionServiceImpl(restTemplate);
    }

    @Test
    public void fetchRoleInGroupTest() throws MopsException {
        String user = "Carlo";
        Set<GroupPermission> groups = Set.of(new GroupPermission(groupId, "admin"));
        Permission permission = new Permission(user, groups);
        Account carlo = Account.of(user, "carlo@hhu.de", "admin");

        when(restTemplate.getForObject(URL, Permission.class)).thenReturn(permission);

        String userRole = permissionService.fetchRoleForUserInGroup(carlo, groupId);

        assertThat(userRole).isEqualTo("admin");
    }

    @Test
    public void fetchRolesInGroupTest() throws MopsException {
        GroupPermission[] roles = { new GroupPermission(groupId, "admin"), new GroupPermission(groupId, "editor") };
        when(restTemplate.getForObject(URL, GroupPermission[].class)).thenReturn(roles);

        Set<String> rolesInGroup = permissionService.fetchRolesInGroup(groupId);

        assertThat(rolesInGroup).containsExactlyInAnyOrder("admin", "editor");
    }
}
