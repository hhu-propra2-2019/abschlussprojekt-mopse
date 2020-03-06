package mops.presentation;

import mops.businesslogic.*;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DirectoryControllerTest {
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
     * Setups the a Mock MVC Builder.
     */
    @BeforeEach
    public void setUp() {
        ArrayList<FileInfo> files = new ArrayList<>();
        final Account account = new Account("studi", "bla@bla.de", "pic.png", Set.of("studentin"));
        given(fileService.getAllFilesOfGroup(account, 1)).willReturn(files);
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
        SecurityContextUtil.setupSecurityContextMock("userName", "userEmail@mail.de", Set.of("studentin"));
        mvc.perform(get("/material1/dir/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("directory"));
    }

    /**
     * Tests the route after uploading a file.
     */
    @Test
    public void uploadFile() throws Exception {
        SecurityContextUtil.setupSecurityContextMock("userName", "userEmail@mail.de", Set.of("studentin"));
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
        SecurityContextUtil.setupSecurityContextMock("userName", "userEmail@mail.de", Set.of("studentin"));
        mvc.perform(post("/material1/dir/1/create")
                .with(csrf())
                .requestAttr("folderName", "Vorlesungen"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/material1/dir/2"));
    }

    /**
     * Tests the route after searching a folder.
     */
    @Test
    public void searchFolder() throws Exception {
        SecurityContextUtil.setupSecurityContextMock("userName", "userEmail@mail.de", Set.of("studentin"));
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
        SecurityContextUtil.setupSecurityContextMock("userName", "userEmail@mail.de", Set.of("studentin"));
        mvc.perform(delete("/material1/dir/1")
                .requestAttr("dirId", 1)
                .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("directory"));
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
