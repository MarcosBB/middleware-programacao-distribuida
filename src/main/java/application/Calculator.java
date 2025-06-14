package application;

import middleware.annotations.*;

@RemoteComponent(name = "Calculator")
public class Calculator {
    @RemoteMethod(name = "add")
    public int add(int a, int b) {
        return a + b;
    }

    @RemoteMethod(name = "subtract")
    public int subtract(int a, int b) {
        return a - b;
    }

    @RemoteMethod(name = "multiply")
    public int multiply(int a, int b) {
        return a * b;
    }

    @RemoteMethod(name = "divide")
    public int divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Division by zero is not allowed.");
        }
        return a / b;
    }
}
