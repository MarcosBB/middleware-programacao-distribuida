package middleware.invoker;

public class InvocationRequest {
    private String object;
    private String method;
    private Object[] parameters;
    private Class<?>[] parameterTypes;
    private String requestType;

    public InvocationRequest() {}

    public InvocationRequest(String object, String method, Object[] parameters, Class<?>[] parameterTypes) {
        this.object = object;
        this.method = method;
        this.parameters = parameters;
        this.parameterTypes = parameterTypes;
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

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
