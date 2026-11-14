package com.example.homework.calc;

import org.springframework.stereotype.Component;

/** Test menu: business logic of the four-operation calculator (tested by CalculatorTest). */
@Component
public class Calculator {

    public int add(int a, int b) { return a + b; }

    public int subtract(int a, int b) { return a - b; }

    public int multiply(int a, int b) { return a * b; }

    public double divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("You cannot divide by zero!");
        }
        return (double) a / b;
    }
}
