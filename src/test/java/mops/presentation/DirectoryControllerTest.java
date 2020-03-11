package mops.presentation;

import com.c4_soft.springaddons.test.security.context.support.WithIDToken;
import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import com.c4_soft.springaddons.test.security.web.servlet.request.keycloak.ServletKeycloakAuthUnitTestingSupport;
import mops.businesslogic.DirectoryService;
import mops.businesslogic.FileQuery;
import mops.businesslogic.FileService;
import mops.businesslogic.GroupService;
import mops.exception.MopsException;
import mops.persistence.FileRepository;
import mops.persistence.file.FileInfo;
import mops.utils.KeycloakContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@KeycloakContext
@WebMvcTest(DirectoryController.class)
class DirectoryControllerTest extends ServletKeycloakAuthUnitTestingSupport {

    @MockBean
    FileRepository fileRepository;
    @MockBean
    GroupService groupService;
    @MockBean
    FileService fileService;
    @MockBean
    DirectoryService directoryService;

    /**
     * Setups the a Mock MVC Builder.
     */
    @BeforeEach
    void setup() throws MopsException {
        given(directoryService.getSubFolders(any(), eq(1L))).willReturn(List.of());
        given(fileService.getFilesOfDirectory(any(), eq(1L))).willReturn(List.of());
        doNothing().when(directoryService).uploadFile(any(), eq(1L), any());
        given(directoryService.createFolder(any(), eq(1L), any())).willReturn(2L);
        given(directoryService.deleteFolder(any(), eq(1L))).willReturn(0L);
        given(directoryService.searchFolder(any(), eq(1L), any())).willReturn(List.of());
    }

    /**
     * Tests if the correct view is returned for showing content of a folder.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void showContent() throws Exception {
        mockMvc().perform(get("/material1/dir/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("directory"));
    }

    /**
     * Tests the route after uploading a file.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void uploadFile() throws Exception {
        mockMvc().perform(post("/material1/dir/1/upload")
                .requestAttr("file", mock(FileInfo.class))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/material1/dir/1"));
    }

    /**
     * Tests the route after creating a sub folder. It should be the new folder.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void createFolder() throws Exception {
        mockMvc().perform(post("/material1/dir/1/create")
                .requestAttr("folderName", "Vorlesungen")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/material1/dir/2"));
    }

    /**
     * Tests the route after searching a folder.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void searchFolder() throws Exception {
        mockMvc().perform(post("/material1/dir/1/search")
                .requestAttr("searchQuery", mock(FileQuery.class))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("files"));
    }

    /**
     * Tests if a user can delete a directory.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void deleteDirectory() throws Exception {
        mockMvc().perform(delete("/material1/dir/1")
                .requestAttr("dirId", 1)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/material1/dir/0"));
    }

    /**
     * Test if route is secured.
     */
    @Test
    void notSignedIn() throws Exception {
        mockMvc().perform(get("/material1/dir/1"))
                .andExpect(status().is3xxRedirection());
    }
}
