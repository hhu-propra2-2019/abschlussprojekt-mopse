package mops.businesslogic.gruppenfindung;

import mops.businesslogic.event.LatestEventIdService;
import mops.businesslogic.group.GroupService;
import mops.exception.MopsException;
import mops.persistence.event.LatestEventId;
import mops.persistence.group.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GroupUpdaterTest {

    // TODO: test if the DTO objects are converted to our data objects correctly (adding, deleting etc.)

    @Mock
    GruppenfindungsService gruppenfindungsService;
    @Mock
    LatestEventIdService latestEventIdService;
    @Mock
    GroupService groupService;

    GroupUpdater groupUpdater;
    private LatestEventId latestEventId;

    @BeforeEach
    void setup() throws MopsException {
        groupUpdater = new GroupUpdater(gruppenfindungsService, latestEventIdService, groupService);
        latestEventId = LatestEventId.of();
        given(latestEventIdService.getLatestEventId()).willReturn(latestEventId);
    }

    @Test
    void latestEventIdGetsUpdated() throws MopsException {
        UpdatedGroupsDTO updatedGroups = new UpdatedGroupsDTO();
        updatedGroups.setEventId(20L);
        updatedGroups.setGroupDAOs(List.of());
        given(gruppenfindungsService.getUpdatedGroups(anyLong())).willReturn(updatedGroups);

        groupUpdater.updateDatabase();

        assertThat(latestEventId.getEventId()).isEqualTo(20L);
    }

    @Test
    public void addingGroupTest() throws MopsException {
        UUID groupId = new UUID(0, 6);
        String groupName = "test";
        String userName = "Thabb";

        UpdatedGroupsDTO updatedGroups = new UpdatedGroupsDTO();
        updatedGroups.setEventId(21L);

        UserDTO user = new UserDTO();
        user.setUsername(userName);

        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setGroupId(groupId);
        groupDTO.setGroupName(groupName);
        groupDTO.setStatus(StatusDTO.ACTIVE);

        updatedGroups.setGroupDAOs(List.of(groupDTO));

        Group expectedGroup = Group.builder()
                .groupId(groupId)
                .name(groupName)
                .member(userName, "admin")
                .build();

        given(gruppenfindungsService.getUpdatedGroups(anyLong())).willReturn(updatedGroups);
        given(gruppenfindungsService.getMembers(groupId)).willReturn(List.of(user));
        given(gruppenfindungsService.isUserAdminInGroup(userName, groupId)).willReturn(true);

        groupUpdater.updateDatabase();

        verify(groupService).saveAllGroups(List.of(expectedGroup));
    }

    @Test
    public void deleteGroupTest() throws MopsException {
        UUID groupId = new UUID(0, 7);
        String groupName = "test";
        String userName = "Thabb";

        UpdatedGroupsDTO updatedGroups = new UpdatedGroupsDTO();
        updatedGroups.setEventId(22L);

        UserDTO user = new UserDTO();
        user.setUsername(userName);

        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setGroupId(groupId);
        groupDTO.setGroupName(groupName);
        groupDTO.setStatus(StatusDTO.DEACTIVATED);

        updatedGroups.setGroupDAOs(List.of(groupDTO));

        Group expectedGroup = Group.builder()
                .id(33L)
                .groupId(groupId)
                .name(groupName)
                .member(userName, "admin")
                .build();

        given(gruppenfindungsService.getUpdatedGroups(anyLong())).willReturn(updatedGroups);
        given(groupService.findGroupByGroupId(groupId)).willReturn(Optional.of(expectedGroup));

        groupUpdater.updateDatabase();

        verify(groupService).deleteAllGroups(List.of(expectedGroup.getId()));
    }
}
