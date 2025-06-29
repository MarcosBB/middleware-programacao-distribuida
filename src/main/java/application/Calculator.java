package application;

import middleware.annotations.*;

import java.util.UUID;

@RemoteComponent(name = "Calculator")
@InstanceScope("PerRequest")
public class Calculator {

    private UUID instanceUUID;

    public Calculator(UUID uuid) {
        instanceUUID = uuid;
        
    }

    @RemoteMethod(name = "add")
    public String add(int a, int b) {
        return "ID=" + instanceUUID + ", Resultado=" + (a + b);
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
        if (b == 0) throw new IllegalArgumentException("Division by zero is not allowed.");
        return a / b;
    }
}
