package com.example.homework;

import com.example.homework.calc.CalculatorController;
import com.example.homework.forex.ForexController;
import com.example.homework.soap.SoapController;
import com.oanda.v20.RequestException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Shows a themed error page instead of the white-label page when a remote call fails
 * (e.g. MNB unreachable, wrong Oanda token, Forex market closed at the weekend).
 * Only for the HTML pages: the REST API of the React menu keeps returning JSON errors.
 */
@ControllerAdvice(assignableTypes = {MainController.class, SoapController.class,
        ForexController.class, CalculatorController.class})
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handle(Exception e, Model model) throws Exception {
        // let missing static files (e.g. favicon.ico) produce a normal 404
        if (e instanceof org.springframework.web.servlet.resource.NoResourceFoundException) {
            throw e;
        }
        model.addAttribute("errorMessage", describe(e));
        return "error_page";
    }

    /** Finds the most useful message in the exception chain. */
    private String describe(Throwable e) {
        Throwable root = e;
        for (Throwable t = e; t != null; t = (t.getCause() == t ? null : t.getCause())) {
            // Oanda keeps its details (HTTP status, error code, message) in its own fields
            if (t instanceof RequestException re) {
                return "Oanda answered: " + re;   // e.g. "HTTP 401 : Insufficient authorization to perform request."
            }
            root = t;
        }
        return root.getMessage() != null ? root.getMessage() : root.getClass().getSimpleName();
    }
}
