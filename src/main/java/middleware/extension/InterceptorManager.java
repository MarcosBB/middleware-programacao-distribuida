package middleware.extension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import middleware.annotations.InterceptThis;

public class InterceptorManager {

    private static Map<String, Interceptor> interceptors;
    private static List<Interceptor> globalInterceptors;

    private static void loadInterceptors() throws Exception {
        if (interceptors != null) {
            return; // Already loaded
        }
        interceptors = new HashMap<>();
        globalInterceptors = new ArrayList<>();

        Reflections reflections = new Reflections("application");
        Set<Class<? extends Interceptor>> classes = reflections.getSubTypesOf(Interceptor.class);
        
        for (Class<? extends Interceptor> clazz : classes) {
            Interceptor interceptor = clazz.getDeclaredConstructor().newInstance();
            if (interceptor.isGlobal()) {
                globalInterceptors.add(interceptor);
            } else {
                interceptors.put(clazz.getSimpleName(), interceptor);
            }
        }
        System.out.println("Loaded " + interceptors.size() + " interceptors, " + globalInterceptors.size() + " global interceptors.");
    }

    public static List<Interceptor> resolveInterceptors(Method method) throws Exception {
        loadInterceptors();

        List<Interceptor> interceptChain = new ArrayList<>(globalInterceptors);
        
        // Check for method-level interceptors
        if (method.isAnnotationPresent(InterceptThis.class)) {
            InterceptThis methodInterceptor = method.getAnnotation(InterceptThis.class);
            for(String interceptorName : methodInterceptor.interceptors()) {
                Interceptor interceptor = interceptors.get(interceptorName);
                if (interceptor != null) {
                    interceptChain.add(interceptor);
                } else {
                    throw new IllegalArgumentException("Interceptor not found: " + interceptorName);
                }
            }
        }
        
        // Sort interceptors by order
        interceptChain.sort(Comparator.comparingInt(Interceptor::getOrder));
        
        return interceptChain;
    }

}
