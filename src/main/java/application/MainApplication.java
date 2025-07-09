package application;
import middleware.broker.Broker;
import middleware.server.HttpProtocol;

public class MainApplication {
    public static void main(String[] args) {
        Broker broker = new Broker(new HttpProtocol());
        broker.start();
    }
}
