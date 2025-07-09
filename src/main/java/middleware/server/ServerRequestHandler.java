package middleware.server;

import middleware.error.RemotingError;
import middleware.extension.ProtocolInterface;
import middleware.invoker.Invoker;

public class ServerRequestHandler {

    private final Invoker invoker;
    private final ProtocolInterface protocol;

    public ServerRequestHandler(Invoker invoker, ProtocolInterface protocol) {
        this.invoker = invoker;
        this.protocol = protocol;
    }

    public void start() {
        try {
            protocol.start();
            System.out.println("Servidor " + protocol.getProtocolType() + " iniciado na porta " + protocol.getPort());

            while (true) {
                protocol.handleClient();
                try {
                    String responseBody = invoker.handleRequest(protocol.getRequestBody(), protocol.getRequestLine(), protocol.getIpAddress());
                    protocol.sendResponse(responseBody);
                } catch (RemotingError e) {
                    protocol.handleError(invoker.errorSerializer(e));
                }
                protocol.closeConection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
