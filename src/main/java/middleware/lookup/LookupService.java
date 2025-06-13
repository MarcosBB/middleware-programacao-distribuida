package middleware.lookup;

import java.util.concurrent.ConcurrentHashMap;

public class LookupService {

    private final ConcurrentHashMap<String, Object> registry = new ConcurrentHashMap<>();

    public void register(String id, Object object) {
        registry.put(id, object);
    }

    public Object getObject(String id) {
        return registry.get(id);
    }
}
