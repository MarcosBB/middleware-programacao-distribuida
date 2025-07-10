package middleware.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import middleware.error.RemotingError;
import middleware.extension.ProtocolInterface;

public class HttpProtocol implements ProtocolInterface {

    private int port = 8080;
    private String requestBody;
    private String requestLine;
    private String ipAddress;

    private int statusCode;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    @Override
    public String getProtocolType() {
        return "HTTP/1.1";
    }

    @Override
    public void start() throws Exception {
        serverSocket = new ServerSocket(this.port);
    }

    @Override
    public void handleClient() throws Exception {

        this.clientSocket = serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        this.ipAddress = clientSocket.getInetAddress().toString();

        // Ler a primeira linha do request HTTP
        try {
            this.requestLine = in.readLine();
        } catch (Exception e) {
            throw new RemotingError("Failed to connect: " + e.getMessage());
        }
        System.out.println("Request Line: " + this.requestLine);
        if (requestLine == null || requestLine.isEmpty()) {
            this.statusCode = 404;
            throw new RemotingError("Request line is empty or null");
        }

        // Ler os headers até encontrar uma linha vazia
        int contentLength = 0;
        String line;
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        // Ler o corpo da requisição
        char[] bodyChars = new char[contentLength];
        in.read(bodyChars);
        this.requestBody = new String(bodyChars);
        this.statusCode = 200;

    }

    @Override
    public void sendResponse(String responseBody) {
        try (OutputStream out = clientSocket.getOutputStream();) {
            byte[] bodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);
            String statusText = (statusCode == 200) ? "OK" : "Internal Server Error";

            String response = this.getProtocolType() + " " + statusCode + " " + statusText + "\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + bodyBytes.length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";

            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.write(bodyBytes);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleError(String errosMsg) {
        this.statusCode = 500;
        this.sendResponse(errosMsg);
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
        try {
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
