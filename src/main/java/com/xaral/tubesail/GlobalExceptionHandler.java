package com.xaral.tubesail;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : null;
        model.addAttribute("username", username);
        if (ex instanceof NullPointerException) {
            modelAndView.setViewName("blocks/error-search");
            modelAndView.addObject("message", "Video not found!");
        } else {
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Something went wrong...");
        }
        return modelAndView;
    }
}
