package mops.businesslogic;

import mops.exception.MopsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static mops.businesslogic.PermissionServiceProdImpl.URL;
import static org.assertj.core.api.Assertions.assertThat;
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
    String userName;
    private Account carlo;


    @BeforeEach
    void setup() {
        groupId = 1L;
        restTemplate = mock(RestTemplate.class);
        groupService = new GroupServiceProdImpl(directoryService, restTemplate);
        userName = "Carlo";
        carlo = Account.of(userName, "carlo@hhu.de", "admin");

    }

    @Test
    public void getGroupUrl() {
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
        when(restTemplate.getForObject(URL, Group[].class)).thenReturn(groups);

        List<Group> requestedGroups = groupService.getAllGroups(carlo);

        assertThat(requestedGroups).isEqualTo(expectedGroups);
    }
}