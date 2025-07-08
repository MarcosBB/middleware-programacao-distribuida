package application;
import middleware.broker.Broker;

public class MainApplication {
    public static void main(String[] args) {
        Broker broker = new Broker();
        broker.start();
    }
}
