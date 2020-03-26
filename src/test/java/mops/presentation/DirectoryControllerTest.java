package mops.presentation;

import com.c4_soft.springaddons.test.security.context.support.WithIDToken;
import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import com.c4_soft.springaddons.test.security.web.servlet.request.keycloak.ServletKeycloakAuthUnitTestingSupport;
import mops.businesslogic.directory.DirectoryService;
import mops.businesslogic.file.FileService;
import mops.businesslogic.permission.PermissionService;
import mops.businesslogic.security.SecurityService;
import mops.businesslogic.security.UserPermission;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.permission.DirectoryPermissions;
import mops.presentation.form.FileQueryForm;
import mops.util.KeycloakContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@KeycloakContext
@WebMvcTest(DirectoryController.class)
class DirectoryControllerTest extends ServletKeycloakAuthUnitTestingSupport {

    @MockBean
    DirectoryService directoryService;
    @MockBean
    FileService fileService;
    @MockBean
    SecurityService securityService;
    @MockBean
    PermissionService permissionService;

    /**
     * Setups the a Mock MVC Builder.
     */
    @BeforeEach
    void setup() throws MopsException {
        Directory directory = mock(Directory.class);
        Directory root = mock(Directory.class);

        UserPermission userPermission = new UserPermission(true, true, true);
        DirectoryPermissions permissions = DirectoryPermissions.builder()
                .entry("admin", true, true, true)
                .entry("viewer", true, false, false)
                .build();

        given(directory.getId()).willReturn(2L);
        given(directoryService.getDirectory(eq(1L))).willReturn(root);
        given(directoryService.getSubFolders(any(), eq(1L))).willReturn(List.of());
        given(fileService.getFilesOfDirectory(any(), eq(1L))).willReturn(List.of());
        given(directoryService.createFolder(any(), eq(1L), any())).willReturn(directory);
        given(directoryService.deleteFolder(any(), eq(1L))).willReturn(root);
        given(directoryService.searchFolder(any(), eq(1L), any())).willReturn(List.of());
        given(securityService.getPermissionsOfUser(any(), any())).willReturn(userPermission);
        given(permissionService.getPermissions(any())).willReturn(permissions);
    }

    /**
     * Tests if the correct view is returned for showing content of a folder.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void showContent() throws Exception {
        mockMvc().perform(get("/material1/dir/{dir}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("directory"))
                .andDo(document("index/DirectoryController/{method-name}",
                        pathParameters(
                                parameterWithName("dir").description("The directory id.")
                        )));
    }

    /**
     * Tests the route after uploading a file.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void uploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain",
                "teststring".getBytes(StandardCharsets.UTF_8));
        mockMvc().perform(fileUpload("/material1/dir/{dir}/upload", 1)
                .file(file)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/material1/dir/1"))
                .andDo(document("index/DirectoryController/{method-name}",
                        pathParameters(
                                parameterWithName("dir").description("The directory id.")
                        )));
    }

    /**
     * Tests the route after creating a sub folder. It should be the new folder.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void createFolder() throws Exception {
        mockMvc().perform(post("/material1/dir/{dir}/create", 1)
                .param("folderName", "Vorlesungen")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/material1/dir/2"))
                .andDo(document("index/DirectoryController/{method-name}",
                        pathParameters(
                                parameterWithName("dir").description("The directory id.")
                        )));
    }

    /**
     * Tests the route after searching a folder.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void searchFolder() throws Exception {
        FileQueryForm fileQueryForm = new FileQueryForm();
        fileQueryForm.setNames(new String[] { "cv" });
        fileQueryForm.setOwners(new String[] { "Thabb" });
        fileQueryForm.setTypes(new String[] { "pdf" });
        fileQueryForm.setTags(new String[] { "awesome" });

        mockMvc().perform(post("/material1/dir/{dir}/search", 1)
                .requestAttr("fileQueryForm", fileQueryForm)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("directory"))
                .andDo(document("index/DirectoryController/{method-name}",
                        pathParameters(
                                parameterWithName("dir").description("The directory id.")
                        )));
    }

    /**
     * Tests if a user can delete a directory.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void deleteDirectory() throws Exception {
        mockMvc().perform(post("/material1/dir/{dir}/delete", 1)
                .requestAttr("dirId", 1)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/material1/dir/0"))
                .andDo(document("index/DirectoryController/{method-name}",
                        pathParameters(
                                parameterWithName("dir").description("The directory id.")
                        )));
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
