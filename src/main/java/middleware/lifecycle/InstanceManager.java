package middleware.lifecycle;

public interface InstanceManager {
    Object getInstance(Class<?> clazz, String clientId) throws Exception;
}
