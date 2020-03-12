package mops.presentation;

import com.c4_soft.springaddons.test.security.context.support.WithIDToken;
import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import com.c4_soft.springaddons.test.security.web.servlet.request.keycloak.ServletKeycloakAuthUnitTestingSupport;
import mops.businesslogic.DirectoryService;
import mops.businesslogic.FileInfoService;
import mops.businesslogic.FileService;
import mops.businesslogic.GroupService;
import mops.exception.MopsException;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.DirectoryRepository;
import mops.persistence.FileInfoRepository;
import mops.persistence.FileRepository;
import mops.utils.KeycloakContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@KeycloakContext
@WebMvcTest(GroupsController.class)
class GroupsControllerTest extends ServletKeycloakAuthUnitTestingSupport {

    @MockBean
    DirectoryRepository directoryRepository;
    @MockBean
    DirectoryPermissionsRepository directoryPermissionsRepository;
    @MockBean
    FileInfoRepository fileInfoRepository;
    @MockBean
    FileRepository fileRepository;
    @MockBean
    GroupService groupService;
    @MockBean
    FileService fileService;
    @MockBean
    DirectoryService directoryService;

    /**
     * Setup service/repo mocks.
     */
    @BeforeEach
    void setup() throws MopsException {
        given(groupService.getAllGroups(any())).willReturn(List.of());
    }

    /**
     * Test if all groups are presented in the index view.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void getAllGroups() throws Exception {
        mockMvc().perform(get("/material1/groups"))
                .andExpect(status().isOk())
                .andExpect(view().name("groups"));
    }

    /**
     * Test if route is secured.
     */
    @Test
    void notSignedIn() throws Exception {
        mockMvc().perform(get("/material1/groups"))
                .andExpect(status().is3xxRedirection());
    }
}
