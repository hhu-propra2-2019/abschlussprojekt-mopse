package mops.presentation;

import lombok.extern.slf4j.Slf4j;
import mops.businesslogic.security.Account;
import mops.presentation.error.ExceptionPresentationError;
import mops.presentation.error.MessagePresentationError;
import mops.presentation.error.PresentationError;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
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

        PresentationError presentationError;
        if (ex instanceof MaxUploadSizeExceededException) {
            presentationError = new MessagePresentationError("Die maximale Dateigröße wurde überschritten. "
                    + "Bitte versuche eine kleinere Datei.");
        } else {
            presentationError = new ExceptionPresentationError(ex);
        }

        ModelAndView mav = new ModelAndView("redirect:/material1/error");
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        flashMap.put("error", presentationError);
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
    @SuppressWarnings({ "PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis", "PMD.CyclomaticComplexity" })
    public String handleError(KeycloakAuthenticationToken token, HttpServletRequest request, Model model) {
        if (token != null) {
            Account account = Account.of(token);
            model.addAttribute("account", account);
        }

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

            if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                model.addAttribute("status_message",
                        "Deine Anfrage wurde vom Server nicht verstanden. Jaul!");
            } else if (statusCode == HttpStatus.FORBIDDEN.value() || statusCode == HttpStatus.UNAUTHORIZED.value()) {
                model.addAttribute("status_message", "Das darfst du nicht. Grrrr!");
            } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("status_message",
                        "Die angeforderte Ressource konnte nicht gefunden werden :(");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("status_message", "Captain, wir haben ein Problem! Wuff!");
            } else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                model.addAttribute("status_message", "Diese Methode ist nicht erlaubt.");
            } else if (statusCode == HttpStatus.REQUEST_TIMEOUT.value()) {
                model.addAttribute("status_message", "Das hat uns zu lange gedauert. Zzzzzz.");
            }
        }


        return "mops_error";
    }
}
