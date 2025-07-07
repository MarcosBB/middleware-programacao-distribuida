package application;

import java.util.HashMap;
import java.util.Map;

import middleware.extension.Interceptor;
import middleware.invoker.InvocationRequest;

public class FormatInterceptor implements Interceptor {

    @Override
    public void interceptBefore(InvocationRequest context) {
        // nothing to do here
    }

    @Override
    public Object interceptAfter(InvocationRequest context, Object result) throws Exception {
        switch (context.getObject()) {
            case "calculator":
                return formatCalculator(context, result);
            default:
                return result;
        }
    }

    @Override
    public boolean isGlobal() {
        return true;
    }

    @Override
    public int getOrder() {
        return 1;
    }

    private Object formatCalculator(InvocationRequest context, Object result) throws Exception {
        Calculator calc = (Calculator) context.getInstance();
        Map<String,Object> map = new HashMap<>();
        map.put("requestId", context.getRequestId());
        map.put("clientId", context.getClientId());
        map.put("result", result);
        if (context.getMethod().equals("divide")) {
            int mod = calc.remainder((int) context.getParameters()[0], (int) context.getParameters()[1]);
            map.put("remainder", mod);
        }
        return map;
    }

}
