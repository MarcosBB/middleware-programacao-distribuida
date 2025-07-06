package middleware.annotations;

import middleware.lookup.LookupService;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;

public class AnnotationScanner {

    private final LookupService lookup;

    public AnnotationScanner(LookupService lookup) {
        this.lookup = lookup;
    }

    public void scan(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> components = reflections.getTypesAnnotatedWith(RemoteComponent.class);

        for (Class<?> clazz : components) {
            try {
                RemoteComponent comp = clazz.getAnnotation(RemoteComponent.class);
                lookup.register(comp.value(), clazz);

                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(RemoteMethod.class)) {
                        RemoteMethod rm = method.getAnnotation(RemoteMethod.class);
                        System.out.printf("Registered: %s (%s) ", rm.name(), comp.value());
                    }
                }

            } catch (Exception e) {
                System.err.println("Error scanning component: " + e.getMessage());
            }
        }
    }
}
