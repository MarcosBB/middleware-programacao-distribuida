package middleware.broker;

import middleware.lookup.LookupService;
import middleware.server.ServerRequestHandler;

public class Broker {

    private final LookupService lookup;
    private final ServerRequestHandler server;

    public Broker(LookupService lookup, ServerRequestHandler server) {
        this.lookup = lookup;
        this.server = server;
    }

    public void start() {
        server.start();
    }

    public LookupService getLookup() {
        return lookup;
    }
}
