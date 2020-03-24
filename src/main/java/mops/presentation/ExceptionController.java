package mops.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Exception Handling.
 */
@Controller
public class ExceptionController implements HandlerExceptionResolver {
    /**
     * Exception handling.
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @param ex       Exception
     * @return Error view
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
}
