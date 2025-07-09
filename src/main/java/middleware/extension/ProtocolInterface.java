package middleware.extension;

public interface ProtocolInterface {
    String getProtocolType();
    void start() throws Exception;
    void handleClient() throws Exception;
    void sendResponse(String responseBody);
    void handleError(String errorMsg);
    String getRequestBody();
    String getRequestLine();
    String getIpAddress();
    int getPort();
    void setPort(int port);
    void closeConection();
}
