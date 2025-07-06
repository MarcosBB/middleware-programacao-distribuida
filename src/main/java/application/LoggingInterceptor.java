package application;

import middleware.annotations.Inject;
import middleware.extension.Interceptor;
import middleware.invoker.InvocationRequest;

public class LoggingInterceptor implements Interceptor {

    @Inject
    Logger logger;

    @Override
    public void interceptBefore(InvocationRequest context) {
        System.out.println("[BEFORE] " + context.getObject() + "#" + context.getMethod());
    }

    @Override
    public Object interceptAfter(InvocationRequest context, Object result) {
        System.out.println("[AFTER] Result: " + result);
        return result;
    }

    @Override
    public boolean isGlobal() {
        return true; // This interceptor is global, it will intercept all remote requests
    }

    @Override
    public int getOrder() {
        return 2; // Default order, can be overridden if needed
    }
}
