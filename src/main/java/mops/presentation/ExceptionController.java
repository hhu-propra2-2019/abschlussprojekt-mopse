package mops.presentation;

import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.security.Account;
import mops.presentation.error.ExceptionPresentationError;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Exception Handling.
 */
@Slf4j
@Controller
public class ExceptionController implements HandlerExceptionResolver, ErrorController {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler,
                                         Exception ex) {
        log.error("Caught exception from controller:", ex);
        ModelAndView mav = new ModelAndView("redirect:/material1/error");
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        flashMap.put("error", new ExceptionPresentationError(ex));
        return mav;
    }

    /**
     * Error mapping.
     *
     * @param token   auth token
     * @param request http request
     * @param model   model
     * @return view
     */
    @RequestMapping("/material1/error")
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis" })
    public String handleError(KeycloakAuthenticationToken token, HttpServletRequest request, Model model) {
        Account account = Account.of(token);
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String referer = request.getHeader("referer");
        model.addAttribute("referer", referer);

        if (status != null) {
            int statusCode;
            try {
                statusCode = (int) status;
            } catch (ClassCastException e) {
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            }
            model.addAttribute("statuscode", statusCode);

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("status_message",
                        "Die angeforderte Ressource konnte nicht gefunden werden :(");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("status_message", "Captain, wir haben ein Problem! Wuff!");
            }
        }

        model.addAttribute("account", account);
        return "mops_error";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getErrorPath() {
        return "/material1/error";
    }
}
