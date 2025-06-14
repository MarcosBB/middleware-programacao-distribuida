package application;

import middleware.annotations.*;

@RemoteComponent(name = "Calculator")
public class Calculator {
    @RemoteMethod(name = "add")
    public int add(int a, int b) {
        return a + b;
    }
}
