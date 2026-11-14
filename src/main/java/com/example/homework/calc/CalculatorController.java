package com.example.homework.calc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Locale;

/** Test menu: calculator form (tested by CalculatorControllerTest with MockMvc). */
@Controller
public class CalculatorController {

    private final Calculator calculator;

    public CalculatorController(Calculator calculator) {
        this.calculator = calculator;
    }

    @GetMapping("/calc")
    public String showForm(Model model) {
        model.addAttribute("calcForm", new CalculatorForm());
        return "calc";
    }

    @PostMapping("/calc")
    public String calculate(@ModelAttribute("calcForm") CalculatorForm calcForm, Model model) {
        int a = calcForm.getA();
        int b = calcForm.getB();
        try {
            switch (calcForm.getOp() == null ? "" : calcForm.getOp().toLowerCase(Locale.ROOT)) {
                case "add" -> { model.addAttribute("result", String.valueOf(calculator.add(a, b))); model.addAttribute("symbol", "+"); }
                case "sub" -> { model.addAttribute("result", String.valueOf(calculator.subtract(a, b))); model.addAttribute("symbol", "-"); }
                case "mul" -> { model.addAttribute("result", String.valueOf(calculator.multiply(a, b))); model.addAttribute("symbol", "*"); }
                case "divide" -> {
                    // Locale.ROOT => always a dot as decimal separator (independent of the PC's language)
                    model.addAttribute("result", String.format(Locale.ROOT, "%.2f", calculator.divide(a, b)));
                    model.addAttribute("symbol", "/");
                }
                default -> model.addAttribute("error", "Unknown operation!");
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "calc";
    }
}
