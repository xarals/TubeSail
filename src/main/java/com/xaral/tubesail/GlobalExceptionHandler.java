package com.xaral.tubesail;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        System.out.println(ex.getMessage());
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
