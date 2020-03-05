package mops.presentation;

import mops.businesslogic.GroupService;
import mops.persistence.Directory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SuppressWarnings("PMD")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GroupsControllerTest {


    /**
     * necessary mock until GroupService is implemented.
     */
    @MockBean
    private GroupService groupService;
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
        List<Directory> groupList = new ArrayList<>();
        given(groupService.getAllGroups(1)).willReturn(groupList);
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(print())
                .apply(springSecurity())
                .build();

    }

    /**
     * Test if all groups are presented in the index view.
     */
    @Test
    @WithMockUser(username = "studi", roles = {"studentin"})
    public void getAllGroups() throws Exception {
        Set<String> roles = Sets.newSet("studentin");
        //noinspection rawtypes as is it convention for this principal
        KeycloakPrincipal principal = mock(KeycloakPrincipal.class,
                RETURNS_DEEP_STUBS);
        when((principal).getKeycloakSecurityContext().getIdToken().getNickName()).thenReturn("studi");
        SimpleKeycloakAccount account = new SimpleKeycloakAccount(principal,
                roles,
                mock(RefreshableKeycloakSecurityContext.class));
        KeycloakAuthenticationToken authenticationToken = new KeycloakAuthenticationToken(account, true);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authenticationToken);
        mvc.perform(get("/material1/groups/"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("groups"));
    }

    /**
     * Test if route secured.
     *
     * @throws Exception on error
     */
    @Test
    public void notSignedIn() throws Exception {
        mvc.perform(get("/material1/groups/"))
                .andExpect(status().is3xxRedirection());
    }
}
