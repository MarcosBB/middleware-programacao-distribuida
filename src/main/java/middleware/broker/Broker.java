package middleware.broker;
import middleware.lookup.LookupService;
import middleware.server.ServerRequestHandler;
import middleware.invoker.Invoker;
import middleware.marshaller.JsonMarshaller;
import middleware.annotations.AnnotationScanner;

public class Broker {

    public final LookupService lookup = new LookupService();

    private final JsonMarshaller marshaller = new JsonMarshaller();
    private final Invoker invoker = new Invoker(lookup, marshaller);
    private final ServerRequestHandler server = new ServerRequestHandler(invoker);
    private final AnnotationScanner annotScanner = new AnnotationScanner(lookup);

    public Broker() {}

    public void start() {
        server.start();
    }

    public void register(String serviceName, Class<?> serviceClass) {
        lookup.register(serviceName, serviceClass);
    }

    public void registerAllRemoteComponents() {
        annotScanner.scan("application");
    }

    public LookupService getLookup() {
        return lookup;
    }
}
