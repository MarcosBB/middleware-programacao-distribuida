package application;

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
        if (context.getObject().equals("calculator")) {
            return formatCalculator(context, result);
        }
        return result;
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
        Map<String,Object> map = Map.of(
            "id", calc.getUUID(),
            "result", result
        );
        if (context.getMethod().equals("divide")) {
            int mod = calc.remainder((int) context.getParameters()[0], (int) context.getParameters()[1]);
            map.put("remainder", mod);
        }
        return map;
    }

}
