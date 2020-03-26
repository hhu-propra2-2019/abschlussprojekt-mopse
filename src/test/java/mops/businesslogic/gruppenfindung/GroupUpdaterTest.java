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

    @Mock
    GruppenfindungsService gruppenfindungsService;
    @Mock
    LatestEventIdService latestEventIdService;
    @Mock
    GroupService groupService;

    GroupUpdater groupUpdater;

    LatestEventId latestEventId;
    UUID groupId;
    String groupName;
    String userName;
    UpdatedGroupsDTO updatedGroups;
    UserDTO user;
    GroupDTO groupDTO;

    @BeforeEach
    void setup() throws MopsException {
        groupUpdater = new GroupUpdater(gruppenfindungsService, latestEventIdService, groupService);
        latestEventId = LatestEventId.of();
        given(latestEventIdService.getLatestEventId()).willReturn(latestEventId);

        groupId = new UUID(0, 7);
        groupName = "test";
        userName = "Thabb";

        updatedGroups = new UpdatedGroupsDTO();
        user = new UserDTO();
        user.setUserName(userName);

        groupDTO = new GroupDTO();
        groupDTO.setGroupId(groupId);
        groupDTO.setGroupName(groupName);
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
    void addingGroupTest() throws MopsException {
        updatedGroups.setEventId(21L);
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

        verify(groupService).saveGroup(expectedGroup);
    }

    @Test
    void deleteGroupTest() throws MopsException {
        updatedGroups.setEventId(22L);
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

        verify(groupService).deleteGroup(expectedGroup.getId());
    }
}
