package application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import middleware.annotations.Inject;
import middleware.extension.Interceptor;
import middleware.invoker.InvocationRequest;

public class LoggingInterceptor implements Interceptor {

    @Inject
    Logger logger;

    private Map<String, Long> timeStamps = new ConcurrentHashMap<>();

    @Override
    public void interceptBefore(InvocationRequest context) {
        if (context.getObject().equals("logger")) {
            // Skip logging for logger component to avoid infinite recursion
            return;
        }
        timeStamps.put(context.getRequestId(), System.nanoTime());
        logger.addLog(new Log("[StartInvoke] " + context.getObject() + " - " + context.getMethod(), LogLevel.INFO));
    }

    @Override
    public Object interceptAfter(InvocationRequest context, Object result) {
        if (context.getObject().equals("logger")) {
            // Skip logging for logger component to avoid infinite recursion
            return result;
        }
        Long duration = System.nanoTime() - timeStamps.remove(context.getRequestId());
        logger.addLog(new Log("[EndInvoke] durantion: " + (duration / 1_000_000.0) + " ms | "
                                                        + context.getObject() + " - " 
                                                        + context.getMethod() + " = " + result, LogLevel.INFO));
        return result;
    }

    @Override
    public boolean isGlobal() {
        return true; // This interceptor is global, it will intercept all remote requests
    }

    @Override
    public int getOrder() {
        return 20000;
    }
}
