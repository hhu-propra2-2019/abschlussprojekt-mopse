package mops.presentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SuppressWarnings("PMD")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GroupsControllerTest {

    /**
     * Necessary bean.
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * Necessary bean.
     */
    private MockMvc mvc;

    /**
     * Setups the a Mock MVC Builder.
     */
    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

    }

    /**
     * Test if all groups are presented in the index view.
     */
    @Test
    public void getAllGroups() throws Exception {
        mvc.perform(get("/material1/groups/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("Index"));
    }
}
