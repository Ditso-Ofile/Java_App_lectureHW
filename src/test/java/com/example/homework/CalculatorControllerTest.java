package com.example.homework;

import com.example.homework.calc.Calculator;
import com.example.homework.calc.CalculatorController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Slice tests (MockMvc) for the calculator controller and form. */
@WebMvcTest(CalculatorController.class)
@Import(Calculator.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test @DisplayName("GET /calc - default form is loaded")
    void testShowForm() throws Exception {
        mockMvc.perform(get("/calc"))
                .andExpect(status().isOk())
                .andExpect(view().name("calc"))
                .andExpect(model().attributeExists("calcForm"))
                .andExpect(model().attribute("calcForm", hasProperty("op", is("add"))))
                .andExpect(model().attribute("calcForm", hasProperty("a", is(0))))
                .andExpect(model().attribute("calcForm", hasProperty("b", is(0))));
    }

    @Test @DisplayName("POST /calc - addition")
    void testAddition() throws Exception {
        mockMvc.perform(post("/calc").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("a", "12").param("b", "8").param("op", "add"))
                .andExpect(status().isOk())
                .andExpect(view().name("calc"))
                .andExpect(model().attribute("calcForm", hasProperty("a", is(12))))
                .andExpect(model().attribute("calcForm", hasProperty("b", is(8))))
                .andExpect(model().attribute("result", is("20")))
                .andExpect(model().attribute("symbol", is("+")));
    }

    @Test @DisplayName("POST /calc - subtraction")
    void testSubtraction() throws Exception {
        mockMvc.perform(post("/calc").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("a", "5").param("b", "9").param("op", "sub"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("result", is("-4")))
                .andExpect(model().attribute("symbol", is("-")));
    }

    @Test @DisplayName("POST /calc - multiplication")
    void testMultiplication() throws Exception {
        mockMvc.perform(post("/calc").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("a", "6").param("b", "7").param("op", "mul"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("result", is("42")))
                .andExpect(model().attribute("symbol", is("*")));
    }

    @Test @DisplayName("POST /calc - division with 2 decimals")
    void testDivision() throws Exception {
        mockMvc.perform(post("/calc").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("a", "5").param("b", "2").param("op", "divide"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("result", is("2.50")))
                .andExpect(model().attribute("symbol", is("/")));
    }

    @Test @DisplayName("POST /calc - division by zero gives an error message")
    void testDivisionByZero() throws Exception {
        mockMvc.perform(post("/calc").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("a", "10").param("b", "0").param("op", "divide"))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("result"))
                .andExpect(model().attribute("error", is("You cannot divide by zero!")));
    }
}
