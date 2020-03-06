package mops.presentation;

import mops.businesslogic.Account;
import mops.businesslogic.FileQuery;
import mops.businesslogic.FileService;
import mops.businesslogic.GroupService;
import mops.persistence.FileInfo;
import mops.presentation.utils.SecurityContextUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GroupControllerTest {
    /**
     * Necessary mock until GroupService is implemented.
     */
    @MockBean
    private GroupService groupService;
    /**
     * Necessary mock until GroupService is implemented.
     */
    @MockBean
    private FileService fileService;

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
    void setUp() {
        ArrayList<FileInfo> files = new ArrayList<>();
        final Account account = new Account("studi", "bla@bla.de", "pic.png", Set.of("studentin"));
        given(fileService.getAllFilesOfGroup(account, 1)).willReturn(files);
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(print())
                .apply(springSecurity())
                .build();
    }

    /**
     * Tests if all files of the a group are returned.
     */
    @Test
    public void getAllFilesOfDirectory() throws Exception {
        SecurityContextUtil.setupSecurityContextMock("userName", "userEmail@mail.de", Set.of("studentin"));
        mvc.perform(get("/material1/group/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("files"));
    }

    /**
     * Tests if the correct view is called upon searching in a group.
     */
    @Test
    public void searchFile() throws Exception {
        FileQuery fileQuery = mock(FileQuery.class);
        SecurityContextUtil.setupSecurityContextMock("userName",
                "userEmail@mail.de",
                Set.of("studentin"));
        mvc.perform(post("/material1/group/1/search")
                .requestAttr("searchQuery", fileQuery)
                .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("files"));
    }

    /**
     * Test if route secured.
     *
     * @throws Exception on error
     */
    @Test
    public void notSignedIn() throws Exception {
        mvc.perform(get("/material1/group/1"))
                .andExpect(status().is3xxRedirection());
    }

}
