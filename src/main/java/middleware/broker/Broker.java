package middleware.broker;
import middleware.lookup.LookupService;
import middleware.server.ServerRequestHandler;
import middleware.invoker.Invoker;
import middleware.marshaller.JsonMarshaller;

public class Broker {

    public final LookupService lookup = new LookupService();

    private final JsonMarshaller marshaller = new JsonMarshaller();
    private final Invoker invoker = new Invoker(lookup, marshaller);
    private final ServerRequestHandler server = new ServerRequestHandler(invoker);


    public Broker() {}

    public void start() {
        server.start();
    }

    public void register(String serviceName, Class<?> serviceClass) {
        lookup.register(serviceName, serviceClass);
    }

    public LookupService getLookup() {
        return lookup;
    }
}
