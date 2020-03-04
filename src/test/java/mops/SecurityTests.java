package mops;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SuppressWarnings("PMD")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityTests {

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
                .apply(springSecurity())
                .build();
    }


    /**
     * Tests auth needed on index.
     * @throws Exception on error
     */
    @Test
    public void notSignedIn() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Tests get request as authenticated but not right role for monitoring.
     * @throws Exception on error
     */
    @Test
    @WithMockUser("randomUser")
    public void signedInasNormalUser() throws Exception {
        mvc.perform(get("/"))
//                TODO change when route "/" (or similar) exists
//                .andExpect(status().isOk());
                .andExpect(status().isNotFound());

        mvc.perform(get("/actuator/"))
                .andExpect(status().isForbidden());
    }

    /**
     * prometheus should get access to /actuator/.
     * @throws Exception on error
     */
    @Test
    @WithMockUser(username = "prometheus", roles = {"monitoring"})
    public void prometheusShouldHaveAccess() throws Exception {
        mvc.perform(get("/actuator/"))
                .andExpect(status().isOk());
    }
}
