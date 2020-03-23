package mops.businesslogic.group;

import mops.businesslogic.exception.GruppenFindungException;
import mops.businesslogic.security.Account;
import mops.exception.MopsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
// TODO: re-enable once implemented
@Disabled("functionality not implemented yet")
class GroupServiceTest {

    @Mock
    RestTemplate restTemplate;

    GroupService groupService;

    long groupId;
    String userName;
    Account carlo;

    @BeforeEach
    void setup() {
        groupId = 1L;
        userName = "Carlo";
        carlo = Account.of(userName, "carlo@hhu.de", "admin");
        groupService = new GroupServiceProdImpl(restTemplate);
    }

    @Test
    void fetchAllGroupsForUserTest() throws MopsException {
        Group groupOne = new Group(1L, "test1");
        Group groupTwo = new Group(2L, "test2");

        Group[] groups = { groupOne, groupTwo };

        List<Group> expectedGroups = List.of(
                groupOne,
                groupTwo
        );
        when(restTemplate.getForObject(endsWith("/get-all-groups-from-user"), eq(Group[].class))).thenReturn(groups);

        List<Group> requestedGroups = groupService.getUserGroups(carlo);

        assertThat(requestedGroups).isEqualTo(expectedGroups);
    }


    @Test
    void fetchRoleInGroupTest() throws MopsException {
        Set<GroupServiceProdImpl.GroupPermission> groups = Set.of(
                new GroupServiceProdImpl.GroupPermission(groupId, "admin")
        );
        GroupServiceProdImpl.Permission permission = new GroupServiceProdImpl.Permission(userName, groups);

        when(restTemplate.getForObject(endsWith("/get-permission"), eq(GroupServiceProdImpl.Permission.class)))
                .thenReturn(permission);

        String userRole = groupService.getUserRole(carlo, groupId);

        assertThat(userRole).isEqualTo("admin");
    }

    @Test
    void fetchRolesInGroupTest() throws MopsException {
        GroupServiceProdImpl.GroupPermission[] roles = {
                new GroupServiceProdImpl.GroupPermission(groupId, "admin"),
                new GroupServiceProdImpl.GroupPermission(groupId, "editor")
        };
        when(restTemplate.getForObject(endsWith("/get-roles"), eq(GroupServiceProdImpl.GroupPermission[].class)))
                .thenReturn(roles);

        Set<String> rolesInGroup = groupService.getRoles(groupId);

        assertThat(rolesInGroup).containsExactlyInAnyOrder("admin", "editor");
    }

    @Test
    void fetchRoleExceptionThrownTest() {
        when(restTemplate.getForObject(endsWith("/get-permission"), eq(GroupServiceProdImpl.Permission.class)))
                .thenReturn(null);

        assertThatExceptionOfType(GruppenFindungException.class)
                .isThrownBy(() -> groupService.getUserRole(carlo, groupId));
    }

    @Test
    void fetchRolesExceptionThrownTest() {
        when(restTemplate.getForObject(endsWith("/get-roles"), eq(GroupServiceProdImpl.GroupPermission[].class)))
                .thenReturn(null);
        assertThatExceptionOfType(GruppenFindungException.class)
                .isThrownBy(() -> groupService.getRoles(groupId));
    }
}
