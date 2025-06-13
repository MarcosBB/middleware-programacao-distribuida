package middleware.invoker;

import middleware.lookup.LookupService;
import middleware.marshaller.JsonMarshaller;
import middleware.error.RemotingError;

import java.lang.reflect.Method;

public class Invoker {

    private final LookupService lookup;
    private final JsonMarshaller marshaller;

    public Invoker(LookupService lookup, JsonMarshaller marshaller) {
        this.lookup = lookup;
        this.marshaller = marshaller;
    }

    public String handleRequest(String requestJson) throws RemotingError {
        try {
            InvocationRequest req = marshaller.deserialize(requestJson, InvocationRequest.class);
            Object obj = lookup.getObject(req.getObject());
            Method method = obj.getClass().getMethod(req.getMethod(), req.getParameterTypes());
            Object result = method.invoke(obj, req.getParameters());
            return marshaller.serialize(result);
        } catch (Exception e) {
            throw new RemotingError(e.getMessage());
        }
    }
}
