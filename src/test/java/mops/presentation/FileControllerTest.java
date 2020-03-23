package mops.presentation;

import com.c4_soft.springaddons.test.security.context.support.WithIDToken;
import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import com.c4_soft.springaddons.test.security.web.servlet.request.keycloak.ServletKeycloakAuthUnitTestingSupport;
import mops.businesslogic.file.FileContainer;
import mops.businesslogic.file.FileService;
import mops.exception.MopsException;
import mops.persistence.directory.Directory;
import mops.persistence.file.FileInfo;
import mops.util.KeycloakContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@KeycloakContext
@WebMvcTest(FileController.class)
class FileControllerTest extends ServletKeycloakAuthUnitTestingSupport {

    @MockBean
    FileService fileService;

    /**
     * File Info for testing.
     */
    FileInfo fileInfo;
    /**
     * File Contents for testing.
     */
    byte[] fileContent;

    /**
     * Setup service/repo mocks.
     */
    @BeforeEach
    void setup() throws MopsException {
        fileContent = "test".getBytes(StandardCharsets.UTF_8);
        fileInfo = FileInfo.builder()
                .name("file")
                .directory(2L)
                .type(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .size(fileContent.length)
                .owner("user")
                .build();

        Resource resource = new InputStreamResource(new ByteArrayInputStream(fileContent));
        FileContainer fileContainer = new FileContainer(fileInfo, resource);

        given(fileService.getFileInfo(any(), eq(1L))).willReturn(fileInfo);
        given(fileService.getFile(any(), eq(1L))).willReturn(fileContainer);
        Directory dir = mock(Directory.class);
        when(dir.getId()).thenReturn(2L);
        given(fileService.deleteFile(any(), eq(1L))).willReturn(dir);
    }

    /**
     * Tests the route for getting the file info.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void getFileInfo() throws Exception {
        mockMvc().perform(get("/material1/file/{fileId}", 1)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("file"))
                .andDo(document("index/DirectoryController/{method-name}",
                        pathParameters(
                                parameterWithName("fileId").description("The file id.")
                        )));
    }

    /**
     * Tests the route for downloading a file preview.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void downloadFile() throws Exception {
        MvcResult result = mockMvc().perform(get("/material1/file/{fileId}/download", 1)
                .with(csrf())
                .contentType(MediaType.ALL))
                .andExpect(status().isOk())
                .andDo(document("index/FileController/{method-name}",
                        pathParameters(
                                parameterWithName("fileId").description("The file id.")
                        )))
                .andReturn();

        assertThat(result.getResponse().getContentType()).isEqualTo(fileInfo.getType());
        assertThat(result.getResponse().getContentLength()).isEqualTo(fileInfo.getSize());
        assertThat(result.getResponse().getContentAsByteArray()).isEqualTo(fileContent);
    }

    /**
     * Tests if a user can delete a file.
     */
    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void deleteFile() throws Exception {
        mockMvc().perform(post("/material1/file/{fileId}", 1)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect((redirectedUrl("/material1/dir/2")))
                .andDo(document("index/FileController/{method-name}",
                        pathParameters(
                                parameterWithName("fileId").description("The file id.")
                        )));
    }

    /**
     * Test if route secured.
     */
    @Test
    void notSignedIn() throws Exception {
        mockMvc().perform(get("/material1/file/1"))
                .andExpect(status().is3xxRedirection());
    }
}
