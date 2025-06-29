package middleware.lookup;

import java.util.concurrent.ConcurrentHashMap;

public class LookupService {
    private final ConcurrentHashMap<String, Class<?>> classRegistry = new ConcurrentHashMap<>();

    public void register(String id, Class<?> clazz) {
        classRegistry.put(id, clazz);
    }

    public Class<?> getClass(String className) {
        return classRegistry.get(className);
    }
}