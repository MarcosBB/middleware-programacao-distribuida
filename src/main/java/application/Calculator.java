package application;

import middleware.annotations.*;

import java.util.UUID;

@RemoteComponent(name = "calculator")
@InstanceScope("PerRequest")
public class Calculator {

    private UUID instanceUUID;

    public Calculator(UUID uuid) {
        instanceUUID = uuid;
    }

    @RemoteMethod(name = "add", requestType = "POST")
    public int add(int a, int b) {
        return a + b;
    }

    @RemoteMethod(name = "subtract", requestType = "POST")
    public int subtract(int a, int b) {
        return a - b;
    }

    @RemoteMethod(name = "multiply", requestType = "POST")
    public int multiply(int a, int b) {
        return a * b;
    }

    @RemoteMethod(name = "divide", requestType = "POST")
    public int divide(int a, int b) throws Exception {
        if (b == 0) throw new Exception("Division by zero is not allowed.");
        return a / b;
    }

    @RemoteMethod(name = "remainder", requestType = "POST")
    public int remainder(int a, int b) throws Exception {
        if (b == 0) throw new Exception("Division by zero is not allowed.");
        return a % b;
    }

    public UUID getUUID() {
        return instanceUUID;
    }
}
