package mops;

import com.c4_soft.springaddons.test.security.context.support.WithIDToken;
import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import mops.businesslogic.DirectoryService;
import mops.businesslogic.FileService;
import mops.businesslogic.GroupService;
import mops.persistence.DirectoryPermissionsRepository;
import mops.persistence.DirectoryRepository;
import mops.persistence.FileInfoRepository;
import mops.persistence.FileRepository;
import mops.utils.TestContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestContext
@SpringBootTest
class SecurityTests {

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

    @Autowired
    MockMvc mvc;

    /**
     * Tests auth needed on index.
     */
    @Test
    void notSignedIn() throws Exception {
        mvc.perform(get("/material1/groups"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Tests get request as authenticated but not right role for monitoring.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void signedInAsNormalUser() throws Exception {
        mvc.perform(get("/material1/groups"))
                .andExpect(status().isOk());

        mvc.perform(get("/actuator"))
                .andExpect(status().isForbidden());
    }

    /**
     * prometheus should get access to /actuator.
     */
    @Test
    @WithMockKeycloackAuth(name = "prometheus", roles = "monitoring", idToken = @WithIDToken(email =
            "prometheus@mail.de"))
    public void prometheusShouldHaveAccess() throws Exception {
        mvc.perform(get("/actuator"))
                .andExpect(status().isOk());
    }
}
