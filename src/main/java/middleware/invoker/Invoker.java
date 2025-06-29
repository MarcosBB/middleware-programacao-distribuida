package middleware.invoker;

import middleware.lookup.LookupService;
import middleware.marshaller.JsonMarshaller;
import middleware.annotations.InstanceScope;
import middleware.error.RemotingError;
import middleware.lifecycle.InstanceManager;
import middleware.lifecycle.PerRequestInstanceManager;
import middleware.lifecycle.StaticInstanceManager;

import java.lang.reflect.Method;

public class Invoker {

    private final LookupService lookup;
    private final JsonMarshaller marshaller;
    private InstanceManager instanceManager;

    public Invoker(LookupService lookup, JsonMarshaller marshaller) {
        this.lookup = lookup;
        this.marshaller = marshaller;
    }

    public String handleRequest(String requestJson) throws RemotingError {
        try {
            InvocationRequest req = marshaller.deserialize(requestJson, InvocationRequest.class);

            Class<?> targetClass = lookup.getClass(req.getObject());
            InstanceManager manager = resolveInstanceManager(targetClass);
            Object obj = manager.getInstance(targetClass);
            Method method = obj.getClass().getMethod(req.getMethod(), req.getParameterTypes());
            Object result = method.invoke(obj, req.getParameters());
            return marshaller.serialize(result);
            
        } catch (Exception e) {
            throw new RemotingError(e.getMessage());
        }
    }

    private InstanceManager resolveInstanceManager(Class<?> clazz) {
        if (instanceManager != null) {
            return instanceManager;
        }
        if (clazz.isAnnotationPresent(InstanceScope.class)) {
            String scope = clazz.getAnnotation(InstanceScope.class).value();
            if ("PerRequest".equalsIgnoreCase(scope)) {
                instanceManager = new PerRequestInstanceManager();
                return instanceManager;
            }
        }
        instanceManager = new StaticInstanceManager();
        return instanceManager; // Default
    }
}
