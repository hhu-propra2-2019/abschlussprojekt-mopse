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
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayInputStream;
import java.util.Set;

import static mops.presentation.utils.SecurityContextUtil.setupSecurityContextMock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
     * Necessary mock because the storage server is not online and @SpringBootTest is used.
     */
    @MockBean
    private FileRepository fileRepository;

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
    byte[] fileContent;

    /**
     * Setups the a Mock MVC Builder.
     */
    @BeforeEach
    public void setUp() throws MopsException {
        account = new Account("user", "user@mail.de", "studentin");
        fileContent = new byte[] { 1, 2, 3 };
        Resource resource = new InputStreamResource(new ByteArrayInputStream(fileContent));
        FileInfo info = new FileInfo("file", 2L, MediaType.APPLICATION_OCTET_STREAM_VALUE,
                fileContent.length, "", Set.of());
        FileContainer fileContainer = new FileContainer(info, resource);
        given(fileService.getFile(account, 1)).willReturn(fileContainer);
        given(fileService.deleteFile(account, 1)).willReturn(2L);

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(print())
                .apply(springSecurity())
                .build();
    }

    /**
     * Tests the route for getting a file preview, or downloading if preview is not supported.
     */
    @Test
    public void getFile() throws Exception {

        //Gibt erstellten FileContainer zurück
        setupSecurityContextMock(account);
        MvcResult result = mvc.perform(get("/material1/file/1/download")
                .with(csrf())
                .contentType(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsByteArray()).isEqualTo(fileContent);

    }

    /**
     * Tests if a user can delete a file.
     */
    @Test
    public void deleteFile() throws Exception {

        setupSecurityContextMock(account);
        mvc.perform(delete("/material1/file/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect((redirectedUrl("/material1/dir/2")));
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
