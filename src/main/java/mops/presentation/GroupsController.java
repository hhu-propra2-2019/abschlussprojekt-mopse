package mops.presentation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.Account;
import mops.businesslogic.Group;
import mops.businesslogic.GroupService;
import mops.businesslogic.utils.AccountUtil;
import mops.exception.MopsException;
import mops.presentation.error.ExceptionPresentationError;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller Class for all requests on '/material1/groups'.
 */
@Controller
@RequestMapping("/material1/groups")
@AllArgsConstructor
@Slf4j
// demeter violations in logging
// dataflow/one return violations in try-catch statements
@SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.OnlyOneReturn", "PMD.LawOfDemeter" })
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
    public String getAllGroups(KeycloakAuthenticationToken token, Model model) {
        Account account = AccountUtil.getAccountFromToken(token);
        log.info("All groups are requested for user '{}'.", account.getName());

        List<Group> groups = new ArrayList<>();

        try {
            groups.addAll(groupService.getAllGroupsOfUser(account));
        } catch (MopsException e) {
            log.error("Failed to retrieve user groups for '{}':", account.getName(), e);
            model.addAttribute("error", new ExceptionPresentationError(e));
        }

        model.addAttribute("groups", groups);
        model.addAttribute("account", account);
        return "groups";
    }
}
