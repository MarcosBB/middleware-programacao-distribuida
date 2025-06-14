package application;

//import java.rmi.RemoteException;

import middleware.broker.Broker;


public class MainApplication {
    public static void main(String[] args) {

        Broker broker = new Broker();

        Calculator calc = new Calculator();
        
        broker.register("calculator", calc);

        broker.start();
    }
}
