package application;

import middleware.broker.Broker;
import middleware.server.ServerRequestHandler;
import middleware.invoker.Invoker;
import middleware.lookup.LookupService;
import middleware.marshaller.JsonMarshaller;

public class MainApplication {
    public static void main(String[] args) {
        LookupService lookup = new LookupService();
        JsonMarshaller marshaller = new JsonMarshaller();
        Invoker invoker = new Invoker(lookup, marshaller);
        ServerRequestHandler server = new ServerRequestHandler(invoker, marshaller);
        Broker broker = new Broker(lookup, server);

        Calculator calc = new Calculator();
        lookup.register("Calculator", calc);

        broker.start();
    }
}
