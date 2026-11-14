package com.example.homework;

import com.example.homework.calc.Calculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Unit tests (JUnit) for the Calculator class. */
class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test @DisplayName("Testing addition")
    void testAdd() { assertEquals(20, calculator.add(15, 5)); }

    @Test @DisplayName("Testing subtraction: positive result")
    void testSubtract() { assertEquals(10, calculator.subtract(15, 5)); }

    @Test @DisplayName("Testing subtraction: negative result")
    void testSubtractNegativeResult() { assertEquals(-5, calculator.subtract(5, 10)); }

    @Test @DisplayName("Testing multiplication with positive numbers")
    void testMultiply() { assertEquals(25, calculator.multiply(5, 5)); }

    @Test @DisplayName("Testing multiplication with negative numbers")
    void testMultiplyWithNegativeNumbers() { assertEquals(30, calculator.multiply(-3, -10)); }

    @Test @DisplayName("Multiplication by zero")
    void testMultiplyWithZero() {
        assertEquals(0, calculator.multiply(0, 150));
        assertEquals(0, calculator.multiply(-999, 0));
    }

    @Test @DisplayName("Testing division with normal numbers")
    void testDivide() { assertEquals(2.5, calculator.divide(5, 2)); }

    @Test @DisplayName("Division by zero must throw an exception")
    void testDivideByZero() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> calculator.divide(10, 0));
        assertEquals("You cannot divide by zero!", ex.getMessage());
    }

    @Test @DisplayName("Overflow testing for addition (upper limit)")
    void testAddOverflow() { assertEquals(Integer.MIN_VALUE, calculator.add(Integer.MAX_VALUE, 1)); }

    @Test @DisplayName("Underflow testing for subtraction (lower limit)")
    void testSubtractUnderflow() { assertEquals(Integer.MAX_VALUE, calculator.subtract(Integer.MIN_VALUE, 1)); }

    @Test @DisplayName("Division resulting in an infinite decimal expansion")
    void testDivideInfiniteDecimal() { assertEquals(0.3333333333333333, calculator.divide(1, 3), 1e-16); }
}
