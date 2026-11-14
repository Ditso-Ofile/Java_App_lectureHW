package com.example.homework.calc;

/** Transfers the calculator form data to the controller. */
public class CalculatorForm {
    private int a;
    private int b;
    private String op = "add";

    public int getA() { return a; }
    public void setA(int a) { this.a = a; }

    public int getB() { return b; }
    public void setB(int b) { this.b = b; }

    public String getOp() { return op; }
    public void setOp(String op) { this.op = op; }
}
