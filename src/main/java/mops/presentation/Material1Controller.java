package mops.presentation;

import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.security.Account;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Controller for basic routes.
 */
@Slf4j
@Controller
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
    @GetMapping("/material1")
    public String index(KeycloakAuthenticationToken token, Model model) {
        Account account = Account.of(token);
        log.info("Index page requested by user '{}'.", account.getName());

        model.addAttribute("account", account);
        return "redirect:/material1/groups";
    }

    /**
     * Top Index Route.
     *
     * @return redirect
     */
    @GetMapping("/")
    public String topIndex() {
        return "redirect:/material1";
    }

    /**
     * Logout Route.
     *
     * @param request servlet request
     * @return redirect
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/";
    }
}
