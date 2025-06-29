package middleware.extension;

public class InvocationContext {
    private String className;
    private String methodName;
    private Object[] args;
    private String clientIp;

    public InvocationContext(String className, String methodName, Object[] args, String clientIp) {
        this.className = className;
        this.methodName = methodName;
        this.args = args;
        this.clientIp = clientIp;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
}
