package com.cpo.med.exception.handler;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public String handleUnknownException(Throwable e, Model model) {
        e.printStackTrace();
        model.addAttribute("error", e.getMessage());
        return "error";
    }
}