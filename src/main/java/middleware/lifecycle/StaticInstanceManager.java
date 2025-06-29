package middleware.lifecycle;

import java.util.concurrent.ConcurrentHashMap;

public class StaticInstanceManager implements InstanceManager {
    private final ConcurrentHashMap<Class<?>, Object> instances = new ConcurrentHashMap<>();

    @Override
    public Object getInstance(Class<?> clazz, String clientId) throws Exception {
        return instances.computeIfAbsent(clazz, c -> {
            try {
                return c.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
