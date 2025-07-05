package middleware.extension;

import middleware.invoker.InvocationRequest;

public interface Interceptor {
    void interceptBefore(InvocationRequest context) throws Exception;
    Object interceptAfter(InvocationRequest context, Object result) throws Exception;
    default boolean isGlobal() {
        return false; // Override this method to intercept all remote requests
    }
    default int getOrder() {
        // Override this method to change the order of the interceptor in the chain of interceptation
        // The interceptor with the lowest order is executed first before and last after the method invocation
        return 1000;
    }
}
