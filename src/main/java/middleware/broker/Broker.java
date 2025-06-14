package middleware.broker;

import middleware.lookup.LookupService;
import middleware.server.ServerRequestHandler;



import middleware.invoker.Invoker;

import middleware.marshaller.JsonMarshaller;

public class Broker {

    public final LookupService lookup = new LookupService();

    private final JsonMarshaller marshaller = new JsonMarshaller();
    private final Invoker invoker = new Invoker(lookup, marshaller);
    private final ServerRequestHandler server = new ServerRequestHandler(invoker, marshaller);


    public Broker() {}

    public void start() {
        server.start();
    }

    public void register(String serviceName, Object service) {
        lookup.register(serviceName, service);
    }

    public LookupService getLookup() {
        return lookup;
    }
}
