package middleware.extension;

public class LoggingInterceptor implements Interceptor {
    @Override
    public void before(InvocationContext context) {
        System.out.println("[BEFORE] " + context.getClassName() + "#" + context.getMethodName());
    }

    @Override
    public void after(InvocationContext context, Object result) {
        System.out.println("[AFTER] Result: " + result);
    }
}
