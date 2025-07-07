package middleware.invoker;

import middleware.lookup.LookupService;
import middleware.marshaller.JsonMarshaller;
import middleware.annotations.RemoteMethod;
import middleware.annotations.RemoteMethod.RequestType;
import middleware.error.RemotingError;
import middleware.extension.Interceptor;
import middleware.extension.InterceptorManager;
import middleware.lifecycle.InstanceManager;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class Invoker {

    private final LookupService lookup;
    private final JsonMarshaller marshaller;
    private final InstanceManager instanceManager;

    public Invoker(LookupService lookup, JsonMarshaller marshaller) {
        this.lookup = lookup;
        this.marshaller = marshaller;
        this.instanceManager = new InstanceManager();
    }

    public String handleRequest(String requestJson, String header) throws RemotingError {
        try {
            InvocationRequest context = marshaller.deserialize(requestJson, InvocationRequest.class);

            handleHeader(header, context);

            Class<?> targetClass = lookup.getClass(context.getObject());
            
            instanceManager.getInstance(targetClass, context, false);
            
            Object result = handleInvocation(context);
            return marshaller.serialize(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemotingError(e.getMessage());
        }
    }

    private void handleHeader(String header, InvocationRequest context) throws RemotingError {
        String headerParts [] = header.split(" ");
        String requestType = headerParts[0]; // e.g., "POST"
        String requestPath = headerParts[1]; // e.g., "/invoke"

        context.setRequestType(requestType);

        if (!requestPath.equals("/invoke")) {
            String requestPathParts [] = requestPath.split("/");
            if (requestPathParts.length < 3) {
                throw new RemotingError("Invalid request path: " + requestPath);
            }
            String serviceName = requestPathParts[1];
            String methodName = requestPathParts[2];
            context.setObject(serviceName);
            context.setMethod(methodName);
        }
    }

    public Object handleInvocation(InvocationRequest context) throws Exception {
        Object obj = context.getInstance();
        Class<?> targetClass = obj.getClass();
        Method method = findMethodByRemoteAnnotation(targetClass, context.getMethod(), context.getRequestType());

        instanceManager.resolveInjection(obj, context);

        List<Interceptor> interceptors = InterceptorManager.resolveInterceptors(method);

        for (Interceptor interceptor : interceptors.reversed()) {
            instanceManager.resolveInjection(interceptor, context);
            interceptor.interceptBefore(context);
        }
        Object result = method.invoke(obj, context.getParameters());
        for (Interceptor interceptor : interceptors) {
            result = interceptor.interceptAfter(context, result);
        }
        return result;
    }

    public Method findMethodByRemoteAnnotation(Class<?> clazz, String name, RequestType requestType) {
        for (Method method : clazz.getDeclaredMethods()) {
            RemoteMethod ann = method.getAnnotation(RemoteMethod.class);
            if (ann != null && ann.name().equals(name) && ann.requestType() == requestType) {
                return method;
            }
        }
        return null;
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
