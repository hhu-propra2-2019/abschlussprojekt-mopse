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

        String referrer = request.getHeader("referer");
        ModelAndView mav = new ModelAndView("mops_error");
        mav.getModel().put("error", ex);
        mav.getModel().put("referrer", referrer);
        return mav;
    }

    /**
     * {@inheritDoc}
     */
    @RequestMapping("/error")
    @SuppressWarnings({"PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis"})
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String referrer = request.getHeader("referer");
        model.addAttribute("referrer", referrer);
        String error = "mops_error";

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                error = "error-404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                error = "error-500";
            }
        }

        return error;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getErrorPath() {
        return "mops_error";
    }
}
