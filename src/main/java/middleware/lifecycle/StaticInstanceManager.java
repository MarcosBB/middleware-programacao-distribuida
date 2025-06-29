package middleware.lifecycle;

import java.util.UUID;

public class StaticInstanceManager implements InstanceManager {
    Object instance;
    @Override
    public Object getInstance(Class<?> clazz) throws Exception {
        UUID uuid = UUID.randomUUID();

        if (instance == null) {
            instance = clazz.getDeclaredConstructor(UUID.class).newInstance(uuid);
            return instance;

        }else {
            return instance;
        }
    }
}
