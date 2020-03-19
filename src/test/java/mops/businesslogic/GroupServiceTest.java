package mops.businesslogic;

import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {
    GroupService groupService;

    @Mock
    RestTemplate restTemplate;
    @Mock
    DirectoryService directoryService;

    long groupId;
    long rootDirId;
    String userName;
    private Account carlo;


    @BeforeEach
    void setup() {
        groupId = 1L;
        rootDirId = 1L;
        restTemplate = mock(RestTemplate.class);
        groupService = new GroupServiceProdImpl(directoryService, restTemplate);
        userName = "Carlo";
        carlo = Account.of(userName, "carlo@hhu.de", "admin");

    }

    @Test
    public void getGroupUrl() throws MopsException {
        GroupRootDirWrapper groupRootDirWrapper = new GroupRootDirWrapper(groupId, rootDirId);
        when(directoryService.getOrCreateRootFolder(groupId)).thenReturn(Directory.builder()
                .id(rootDirId)
                .groupOwner(groupId)
                .permissions(1L)
                .name("test")
                .build());

        GroupRootDirWrapper groupUrl = groupService.getGroupUrl(groupId);

        assertThat(groupUrl).isEqualTo(groupRootDirWrapper);
    }

    @Test
    public void fetchAllGroupsForUserTest() throws MopsException {
        Group groupOne = new Group(1L, "test1");
        Group groupTwo = new Group(2L, "test2");

        Group[] groups = { groupOne, groupTwo };

        List<Group> expectedGroups = List.of(
                groupOne,
                groupTwo
        );
        //TODO: needs to be changed to ends with when routes are know.
        when(restTemplate.getForObject(anyString(), eq(Group[].class))).thenReturn(groups);

        List<Group> requestedGroups = groupService.getAllGroups(carlo);

        assertThat(requestedGroups).isEqualTo(expectedGroups);
    }
}