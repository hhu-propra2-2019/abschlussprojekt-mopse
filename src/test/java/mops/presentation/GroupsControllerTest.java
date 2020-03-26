package mops.presentation;

import com.c4_soft.springaddons.test.security.context.support.WithIDToken;
import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import com.c4_soft.springaddons.test.security.web.servlet.request.keycloak.ServletKeycloakAuthUnitTestingSupport;
import mops.businesslogic.group.GroupService;
import mops.exception.MopsException;
import mops.util.KeycloakContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@KeycloakContext
@WebMvcTest(GroupsController.class)
class GroupsControllerTest extends ServletKeycloakAuthUnitTestingSupport {

    @MockBean
    GroupService groupService;

    /**
     * Setup service/repo mocks.
     */
    @BeforeEach
    void setup() throws MopsException {
        given(groupService.getUserGroups(any())).willReturn(List.of());
    }

    /**
     * Test if all groups are presented in the index view.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void getAllGroups() throws Exception {
        mockMvc().perform(get("/material1/groups"))
                .andExpect(status().isOk())
                .andExpect(view().name("groups"))
                .andDo(document("index/GroupsController/{method-name}"));
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
