package middleware.extension;

import middleware.extension.InvocationContext;

public interface Interceptor {
    void before(InvocationContext context);
    void after(InvocationContext context, Object result);
}
