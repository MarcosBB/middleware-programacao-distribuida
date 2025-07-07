package application;

import java.util.HashMap;
import java.util.Map;

import middleware.annotations.Inject;
import middleware.extension.Interceptor;
import middleware.invoker.InvocationRequest;

public class HistoryInterceptor implements Interceptor {

    @Inject
    History history;

    @Override
    public void interceptBefore(InvocationRequest context) {
        // nothing to do here
    }

    @Override
    public Object interceptAfter(InvocationRequest context, Object result) throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("object", context.getObject());
        map.put("method", context.getMethod());
        map.put("clientIp", context.getClientIp());
        map.put("requestType", context.getRequestType());
        map.put("parameters", context.getParameters());
  
        history.addEntry(context.getRequestId(), result, map);
        return result;
    }

}
