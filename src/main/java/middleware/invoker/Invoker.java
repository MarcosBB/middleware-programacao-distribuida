package middleware.invoker;

import middleware.lookup.LookupService;
import middleware.marshaller.JsonMarshaller;
import middleware.annotations.InstanceScope;
import middleware.annotations.RemoteMethod;
import middleware.error.RemotingError;
import middleware.lifecycle.InstanceManager;
import middleware.lifecycle.PerRequestInstanceManager;
import middleware.lifecycle.StaticInstanceManager;

import java.lang.reflect.Method;
import java.util.Map;

public class Invoker {

    private final LookupService lookup;
    private final JsonMarshaller marshaller;
    private InstanceManager instanceManager;

    public Invoker(LookupService lookup, JsonMarshaller marshaller) {
        this.lookup = lookup;
        this.marshaller = marshaller;
    }

    public String handleRequest(String requestJson, String header) throws RemotingError {
        try {
            InvocationRequest req = marshaller.deserialize(requestJson, InvocationRequest.class);

            handleHeader(header, req);

            Class<?> targetClass = lookup.getClass(req.getObject());
            InstanceManager manager = resolveInstanceManager(targetClass);
            Object obj = manager.getInstance(targetClass);
            Method method = findMethodByRemoteAnnotation(targetClass, req.getMethod(), req.getRequestType());
            Object result = method.invoke(obj, req.getParameters());
            return marshaller.serialize(result);
            
        } catch (Exception e) {
            throw new RemotingError(e.getMessage());
        }
    }

    private void handleHeader(String header, InvocationRequest req) throws RemotingError {
        String headerParts [] = header.split(" ");
        String requestType = headerParts[0]; // e.g., "POST"
        String requestPath = headerParts[1]; // e.g., "/invoke"

        req.setRequestType(requestType);

        if (!requestPath.equals("/invoke")) {
            String requestPathParts [] = requestPath.split("/");
            if (requestPathParts.length < 3) {
                throw new RemotingError("Invalid request path: " + requestPath);
            }
            String serviceName = requestPathParts[1];
            String methodName = requestPathParts[2];
            req.setObject(serviceName);
            req.setMethod(methodName);
        }
    }

    public Method findMethodByRemoteAnnotation(Class<?> clazz, String name, String requestType) {
        for (Method method : clazz.getDeclaredMethods()) {
            RemoteMethod ann = method.getAnnotation(RemoteMethod.class);
            if (ann != null && ann.name().equals(name) && ann.requestType().equals(requestType)) {
                return method;
            }
        }
        return null;
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

    public final String errorSerializer(Exception error) {
        try
        {
            return marshaller.serialize(Map.of("error", error.getMessage()));
        } catch (Exception e) {
            return "{\"error\": \"" + error.getMessage() + "\"}";
        }
    }
    
}
