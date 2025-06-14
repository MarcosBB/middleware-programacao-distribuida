package middleware.server;

import middleware.invoker.Invoker;
import middleware.error.RemotingError;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;

public class ServerRequestHandler {

    private final Invoker invoker;

    public ServerRequestHandler(Invoker invoker) {
        this.invoker = invoker;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Servidor HTTP iniciado na porta 8080");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            
        }
    }

    private void handleClient(Socket clientSocket) throws RemoteException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            // Ler a primeira linha do request HTTP
            String requestLine = in.readLine();
            if (requestLine == null || !requestLine.startsWith("POST /invoke")) {
                sendResponse(out, 404, "Not Found");
                return;
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
            String requestBody = new String(bodyChars);

            String responseBody;
            int statusCode = 200;

            try {
                responseBody = invoker.handleRequest(requestBody);
            } catch (RemotingError e) {
                responseBody = "{\"error\": \"" + e.getMessage() + "\"}";
                statusCode = 500;
            }

            sendResponse(out, statusCode, responseBody);

        } catch (IOException e) {
            throw new RemoteException("Erro ao processar requisição do cliente", e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {}
        }
    }

    private void sendResponse(OutputStream out, int statusCode, String responseBody) throws IOException {
        byte[] bodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        String statusText = (statusCode == 200) ? "OK" : "Internal Server Error";

        String response =
                "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + bodyBytes.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";

        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.write(bodyBytes);
        out.flush();
    }
}
