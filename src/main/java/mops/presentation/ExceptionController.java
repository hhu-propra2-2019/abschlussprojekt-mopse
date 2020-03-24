package mops.presentation;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Exception Handling.
 */
@Controller
public class ExceptionController implements HandlerExceptionResolver, ErrorController {
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.LawOfDemeter")
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler, Exception ex) {

        String referer = request.getHeader("referer");
        ModelAndView mav = new ModelAndView("mops_error");
        mav.getModel().put("error", ex);
        mav.getModel().put("referer", referer);
        return mav;
    }

    /**
     * {@inheritDoc}
     */
    @RequestMapping("/material1/error")
    @SuppressWarnings({"PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis"})
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String referer = request.getHeader("referer");
        model.addAttribute("referer", referer);

        if (status != null) {
            Integer statusCode;
            try {
                statusCode = Integer.valueOf(status.toString());
            } catch (NumberFormatException e) {
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            }
            model.addAttribute("statuscode", statusCode);

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("status_message",
                        "Die angeforderte Resource konnte nicht gefunden werden :(");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("status_message",
                        "Captain, wir haben ein Problem! Wuff!");
            }
        }

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
