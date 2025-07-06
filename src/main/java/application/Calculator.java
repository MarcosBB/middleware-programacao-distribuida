package application;

import middleware.annotations.*;
import middleware.annotations.InstanceScope.ScopeType;
import middleware.annotations.RemoteMethod.RequestType;

@RemoteComponent("calculator")
@InstanceScope(ScopeType.PER_REQUEST)
public class Calculator {

    @RemoteMethod(name = "add", requestType = RequestType.POST)
    public int add(int a, int b) {
        return a + b;
    }

    @RemoteMethod(name = "subtract", requestType = RequestType.POST)
    public int subtract(int a, int b) {
        return a - b;
    }

    @RemoteMethod(name = "multiply", requestType = RequestType.POST)
    public int multiply(int a, int b) {
        return a * b;
    }

    @RemoteMethod(name = "divide", requestType = RequestType.POST)
    public int divide(int a, int b) throws Exception {
        if (b == 0) throw new Exception("Division by zero is not allowed.");
        return a / b;
    }

    @RemoteMethod(name = "remainder", requestType = RequestType.POST)
    public int remainder(int a, int b) throws Exception {
        if (b == 0) throw new Exception("Division by zero is not allowed.");
        return a % b;
    }

}
