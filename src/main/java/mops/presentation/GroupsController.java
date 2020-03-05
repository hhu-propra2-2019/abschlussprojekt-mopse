package mops.presentation;

import lombok.AllArgsConstructor;
import mops.businesslogic.GroupService;
import mops.persistence.Directory;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.security.RolesAllowed;
import java.util.List;

/**
 * Controller Class for all requests on '/material1/groups'.
 */
@Controller
@RequestMapping("/material1/groups")
@AllArgsConstructor
public class GroupsController {

    /**
     * Communicator with 'Gruppenfindung'.
     */
    private final GroupService groupService;


    /**
     * @param token authentication token from keycloak server.
     * @param model view model.
     * @return Index View.
     */
    @GetMapping
    @RolesAllowed({"ROLE_orga", "ROLE_studentin"})
    public String getAllGroups(KeycloakAuthenticationToken token, Model model) {
        final int userId = getAccountId(token);
        final List<Directory> groups = groupService.getAllGroups(userId);
        model.addAttribute("groups", groups);
        return "groups";
    }

    private int getAccountId(KeycloakAuthenticationToken token) {
        //noinspection rawtypes as it is convention for this type
        final KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        return principal.getKeycloakSecurityContext().getIdToken().getNickName().hashCode(); //NOPMD
    }
}
