package mops.presentation;

import com.c4_soft.springaddons.test.security.context.support.WithIDToken;
import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import com.c4_soft.springaddons.test.security.web.servlet.request.keycloak.ServletKeycloakAuthUnitTestingSupport;
import mops.Material1Application;
import mops.utils.KeycloakContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@KeycloakContext
@WebMvcTest(Material1Application.class)
class Material1ControllerTest extends ServletKeycloakAuthUnitTestingSupport {

    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void index() throws Exception {
        mockMvc().perform(get("/material1"))
                .andExpect(status().isOk())
                .andExpect(view().name("mops_index"))
                .andDo(document("index/Material1Controller/{method-name}"));
    }

    @Test
    @WithMockKeycloackAuth(roles = "studentin", idToken = @WithIDToken(email = "user@mail.de"))
    void error() throws Exception {
        mockMvc().perform(get("/material1/error"))
                .andExpect(status().isOk())
                .andExpect(view().name("mops_error"))
                .andDo(document("index/Material1Controller/{method-name}"));
    }

    /**
     * Test if route is secured.
     */
    @Test
    void notSignedIn() throws Exception {
        mockMvc().perform(get("/material1"))
                .andExpect(status().is3xxRedirection());
    }
}
