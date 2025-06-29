package middleware.lifecycle;

public class PerRequestInstanceManager implements InstanceManager {
    @Override
    public Object getInstance(Class<?> clazz, String clientId) throws Exception {
        return clazz.getDeclaredConstructor().newInstance();
    }
}
