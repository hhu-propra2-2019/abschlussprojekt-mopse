package mops.presentation;

import mops.SpringTestContext;
import mops.businesslogic.*;
import mops.exception.MopsException;
import mops.persistence.FileRepository;
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
public class DirectoryControllerTest {

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
    public void setUp() throws MopsException {
        account = Account.of("user", "user@mail.de", "studentin");
        given(fileService.getAllFilesOfGroup(account, 1)).willReturn(List.of());
        given(directoryService.createFolder(account, 1, "Vorlesungen")).willReturn(2L);
        given(directoryService.deleteFolder(account, 1)).willReturn(0L);
        given(directoryService.searchFolder(account, 1, mock(FileQuery.class))).willReturn(List.of());
        doNothing().when(directoryService).uploadFile(account, 1, mock(FileInfo.class));
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(print())
                .apply(springSecurity())
                .build();
    }

    /**
     * Tests if the correct view is returned for showing content of a folder.
     */
    @Test
    public void showContent() throws Exception {
        setupSecurityContextMock(account);
        mvc.perform(get("/material1/dir/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("directory"));
    }

    /**
     * Tests the route after uploading a file.
     */
    @Test
    public void uploadFile() throws Exception {
        setupSecurityContextMock(account);
        mvc.perform(post("/material1/dir/1/upload")
                .requestAttr("file", mock(FileInfo.class))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/material1/dir/1"));
    }

    /**
     * Tests the route after creating a sub folder. It should be the new folder.
     */
    @Test
    public void createFolder() throws Exception {
        setupSecurityContextMock(account);
        mvc.perform(post("/material1/dir/1/create")
                .requestAttr("folderName", "Vorlesungen")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/material1/dir/2"));
    }

    /**
     * Tests the route after searching a folder.
     */
    @Test
    public void searchFolder() throws Exception {
        setupSecurityContextMock(account);
        mvc.perform(post("/material1/dir/1/search")
                .requestAttr("searchQuery", mock(FileQuery.class))
                .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("files"));
    }

    /**
     * Tests if a user can delete a directory.
     */
    @Test
    public void deleteDirectory() throws Exception {
        setupSecurityContextMock(account);
        mvc.perform(delete("/material1/dir/1")
                .requestAttr("dirId", 1)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/material1/dir/0"));
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
