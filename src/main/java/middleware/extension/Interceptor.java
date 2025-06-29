package middleware.extension;

public interface Interceptor {
    void before(InvocationContext context);
    void after(InvocationContext context, Object result);
}
