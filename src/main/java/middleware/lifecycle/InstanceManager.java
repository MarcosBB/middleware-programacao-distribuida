package middleware.lifecycle;

public interface InstanceManager {
    Object getInstance(Class<?> clazz) throws Exception;
}
