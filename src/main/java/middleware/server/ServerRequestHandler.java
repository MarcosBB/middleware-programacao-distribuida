package middleware.server;

// import middleware.broker.Broker;
import middleware.invoker.Invoker;
import middleware.marshaller.JsonMarshaller;
import middleware.error.RemotingError;

import static spark.Spark.*;

public class ServerRequestHandler {

    private final Invoker invoker;
    private final JsonMarshaller marshaller;

    public ServerRequestHandler(Invoker invoker, JsonMarshaller marshaller) {
        this.invoker = invoker;
        this.marshaller = marshaller;
    }

    public void start() {
        port(8080);
        post("/invoke", (req, res) -> {
            try {
                String result = invoker.handleRequest(req.body());
                res.type("application/json");
                return result;
            } catch (RemotingError e) {
                res.status(500);
                return "{\"error\": \"" + e.getMessage() + "\"}";
            }
        });
    }
}
