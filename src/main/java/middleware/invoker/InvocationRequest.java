package middleware.invoker;

import middleware.annotations.RemoteMethod.RequestType;

/**
 * Represents a invocation context on a request to invoke a method on a remote object.
 * This class encapsulates the details of the invocation, including
 * the target object, method name, parameters, and their types.
 */
public class InvocationRequest { // InvocationContext

    private String object;
    private String method;
    private Object[] parameters;
    private Class<?>[] parameterTypes;
    private RequestType requestType;

    private String clientIp;

    private String requestId;
    private String clientId;

    private Object instance;

    public InvocationRequest() {}

    public InvocationRequest(String clientid, String object, String method, Object[] parameters, Class<?>[] parameterTypes) {
        this.object = object;
        this.method = method;
        this.parameters = parameters;
        this.parameterTypes = parameterTypes;
        this.clientId = clientid;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = RequestType.valueOf(requestType);
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

}
