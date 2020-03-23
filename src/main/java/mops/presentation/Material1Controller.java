package mops.presentation;

import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.security.Account;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for basic routes.
 */
@Slf4j
@Controller
@RequestMapping("material1")
// demeter violations in logging
@SuppressWarnings("PMD.LawOfDemeter")
public class Material1Controller {

    /**
     * Landing page.
     *
     * @param token user credentials
     * @param model spring view model
     * @return error view template
     */
    @GetMapping
    public String index(KeycloakAuthenticationToken token, Model model) {
        Account account = Account.of(token);
        log.info("Index page requested by user '{}'.", account.getName());

        model.addAttribute("account", account);
        return "mops_index";
    }

    /**
     * Basic error page.
     *
     * @param token user credentials
     * @param model spring view model
     * @return error view template
     */
    @GetMapping("error")
    public String error(KeycloakAuthenticationToken token, Model model) {
        Account account = Account.of(token);
        log.info("Error page requested by user '{}'.", account.getName());

        model.addAttribute("account", account);
        return "mops_error";
    }
}
