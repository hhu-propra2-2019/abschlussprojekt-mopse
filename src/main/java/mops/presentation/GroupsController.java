package mops.presentation;

import lombok.AllArgsConstructor;
import mops.businesslogic.Account;
import mops.businesslogic.Group;
import mops.businesslogic.GroupService;
import mops.businesslogic.utils.AccountUtil;
import mops.exception.MopsException;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
     * @return groups view
     */
    @GetMapping
    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.EmptyCatchBlock" })
    public String getAllGroups(KeycloakAuthenticationToken token, Model model) {
        Account account = AccountUtil.getAccountFromToken(token);
        List<Group> groups = null;
        try {
            groups = groupService.getAllGroups(account);
        } catch (MopsException e) {
            // TODO: Add exception handling, remove PMD warning suppression
        }
        model.addAttribute("groups", groups);
        return "groups";
    }
}
