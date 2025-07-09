package middleware.server;

import middleware.extension.ProtocolInterface;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UdpProtocol implements ProtocolInterface {

    private int port = 8080;
    private String requestBody;
    private String requestLine;
    private String ipAddress;

    private DatagramSocket socket;
    private InetAddress clientAddress;
    private int clientPort;

    @Override
    public String getProtocolType() {
        return "UDP";
    }

    @Override
    public void start() throws Exception {
        this.socket = new DatagramSocket(this.port);
    }

    @Override
    public void handleClient() throws Exception {
        byte[] buffer = new byte[4096];  // ajustável conforme tamanho esperado
        DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);

        socket.receive(requestPacket);

        this.clientAddress = requestPacket.getAddress();
        this.clientPort = requestPacket.getPort();
        this.ipAddress = clientAddress.getHostAddress();

        this.requestBody = new String(requestPacket.getData(), 0, requestPacket.getLength(), StandardCharsets.UTF_8);

        // Como UDP não tem cabeçalhos HTTP, criamos um cabeçalho "falso" padrão
        this.requestLine = "POST /invoke";

    }

    @Override
    public void sendResponse(String responseBody) {
        try {
            byte[] responseData = responseBody.getBytes(StandardCharsets.UTF_8);
            DatagramPacket responsePacket = new DatagramPacket(
                    responseData,
                    responseData.length,
                    clientAddress,
                    clientPort
            );

            socket.send(responsePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleError(String errorMsg) {
        this.sendResponse(errorMsg);
    }

    @Override
    public String getRequestBody() {
        return this.requestBody;
    }

    @Override
    public String getRequestLine() {
        return this.requestLine;
    }

    @Override
    public String getIpAddress() {
        return this.ipAddress;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void closeConection() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
