package mops;

import mops.businesslogic.FileInfoService;
import mops.businesslogic.FileService;
import mops.businesslogic.GroupService;
import mops.businesslogic.PermissionService;
import mops.persistence.FileRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringTestContext
@SpringBootTest
public class SecurityTests {

    /**
     * The server is not available while testing.
     */
    @MockBean
    private FileRepository fileRepository;
    /**
     * Necessary mock until GroupService is implemented.
     */
    @MockBean
    private GroupService groupService;

    /**
     * Necessary mock until FileService is implemented.
     */
    @MockBean
    private FileService fileService;
    /**
     * Necessary mock until FileInfoService is implemented.
     */
    @MockBean
    private FileInfoService fileInfoService;
    /**
     * Necessary mock until PermissionService is implemented.
     */
    @MockBean
    private PermissionService permissionService;
    /**
     * Necessary bean .
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * Necessary bean.
     */
    private MockMvc mvc;

    /**
     * Necessary configuration for Spring Security tests.
     */
    @BeforeAll
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(print())
                .apply(springSecurity())
                .build();
    }

    /**
     * Tests auth needed on index.
     *
     * @throws Exception on error
     */
    @Test
    public void notSignedIn() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Tests get request as authenticated but not right role for monitoring.
     *
     * @throws Exception on error
     */
    @Test
    @WithMockUser("randomUser")
    public void signedInAsNormalUser() throws Exception {
        mvc.perform(get("/"))
                // TODO: change when route "/" (or similar) exists
                // .andExpect(status().isOk());
                .andExpect(status().isNotFound());

        mvc.perform(get("/actuator/"))
                .andExpect(status().isForbidden());
    }

    /**
     * prometheus should get access to /actuator/.
     *
     * @throws Exception on error
     */
    @Test
    @WithMockUser(username = "prometheus", roles = {"monitoring"})
    public void prometheusShouldHaveAccess() throws Exception {
        mvc.perform(get("/actuator/"))
                .andExpect(status().isOk());
    }
}
