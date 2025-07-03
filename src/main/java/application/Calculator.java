package application;

import middleware.annotations.*;
import middleware.error.RemotingError;

import java.util.Map;
import java.util.UUID;

@RemoteComponent(name = "calculator")
@InstanceScope("PerRequest")
public class Calculator {

    private UUID instanceUUID;

    public Calculator(UUID uuid) {
        instanceUUID = uuid;
        
    }

    @RemoteMethod(name = "add", requestType = "POST")
    public Object add(int a, int b) {
        return Map.of(
            "id", instanceUUID,
            "result", a + b
        );
    }

    @RemoteMethod(name = "subtract", requestType = "POST")
    public Object subtract(int a, int b) {
        return Map.of(
            "id", instanceUUID,
            "result", a - b
        );
    }

    @RemoteMethod(name = "multiply", requestType = "POST")
    public Object multiply(int a, int b) {
        return Map.of(
            "id", instanceUUID,
            "result", a * b
        );
    }

    @RemoteMethod(name = "divide", requestType = "POST")
    public Object divide(int a, int b) throws RemotingError {
        if (b == 0) throw new RemotingError("Division by zero is not allowed.");
        return Map.of(
            "id", instanceUUID,
            "result", a / b,
            "remainder", a % b
        );
    }
}
