package middleware.broker;
import middleware.lookup.LookupService;
import middleware.server.ServerRequestHandler;
import middleware.invoker.Invoker;
import middleware.lifecycle.LeasingManager;
import middleware.marshaller.JsonMarshaller;
import middleware.annotations.AnnotationScanner;
import middleware.extension.ProtocolInterface;

public class Broker {

    public final LookupService lookup = new LookupService();

    private final JsonMarshaller marshaller = new JsonMarshaller();
    private final Invoker invoker = new Invoker(lookup, marshaller);
    private final ServerRequestHandler server;
    private final AnnotationScanner annotScanner = new AnnotationScanner(lookup);

    public Broker(ProtocolInterface protocol) {
        this.server = new ServerRequestHandler(invoker, protocol);
    }

    public void start() {
        this.registerAllRemoteComponents();
        LeasingManager.startLeaseMonitor();
        server.start();
        LeasingManager.shutdown();
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
