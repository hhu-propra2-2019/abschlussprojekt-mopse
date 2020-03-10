package mops.presentation;

import mops.SpringTestContext;
import mops.businesslogic.*;
import mops.persistence.file.FileInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static mops.presentation.utils.SecurityContextUtil.setupSecurityContextMock;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringTestContext
@SpringBootTest
public class FileControllerTest {

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
     * Necessary mock until DirectoryService is implemented.
     */
    @MockBean
    private DirectoryService directoryService;
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
     * Wrapper of user credentials.
     */
    private Account account;

    /**
     * Setups the a Mock MVC Builder.
     */
    @BeforeEach
    public void setUp() {
        account = new Account("user", "user@mail.de", "studentin");
        FileInfo f = mock(FileInfo.class);
        given(f.getDirectoryId()).willReturn(1L);
        given(fileService.getFile(account, 1)).willReturn(f);

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(print())
                .apply(springSecurity())
                .build();
    }

    /**
     * Tests if a user can delete a file.
     */
    @Test
    public void deleteFile() throws Exception { //TODO: Implement (copy pasta-ed)

        setupSecurityContextMock(account);
        mvc.perform(delete("/material1/file/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect((redirectedUrl("/material1/dir/1")));
    }

    /**
     * Test if route secured.
     *
     * @throws Exception on error
     */
    @Test
    public void notSignedIn() throws Exception {
        mvc.perform(get("/material1/dir/"))
                .andExpect(status().is3xxRedirection());
    }
}
