package middleware.lifecycle;


import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import middleware.annotations.Inject;
import middleware.annotations.InstanceScope;
import middleware.invoker.InvocationRequest;

public class InstanceManager {

    private Map<String, RemoteInstance> staticInstances;
    private Map<String, RemoteInstance> perRequestInstances;
    private Map<String, Map<String, RemoteInstance>> perClientInstances;

    public InstanceManager() {
        this.staticInstances = new ConcurrentHashMap<>();
        this.perRequestInstances = new ConcurrentHashMap<>();
        this.perClientInstances = new ConcurrentHashMap<>();
    }

    public Object getInstance(Class<?> targeClass, InvocationRequest context, boolean isInjection) throws Exception {
        
        String pooledId = PoolingManager.poolInstance(targeClass); // null if not poolable
        
        InstanceScope scope = targeClass.getAnnotation(InstanceScope.class);
        
        if (scope == null) {
            throw new Exception("Class " + targeClass.getName() + " does not have InstanceScope annotation.");
        }

        if (context.getClientId() == null || context.getClientId().isEmpty()) {
            context.setClientId(UUID.randomUUID().toString());
        }

        if (context.getRequestId() == null || context.getRequestId().isEmpty()) {
            context.setRequestId(UUID.randomUUID().toString());
        }

        switch (scope.value()) {
            case STATIC:
                return getStaticInstance(targeClass, context, isInjection, pooledId);

            case PER_REQUEST:
                return getPerRequestInstance(targeClass, context, isInjection, pooledId);

            case PER_CLIENT:
                return getPerClientInstance(targeClass, context, isInjection, pooledId);    

            default:
                return null;
        }
    }

    private Object getStaticInstance(Class<?> targeClass, InvocationRequest context, boolean isInjection, String pooledId) throws Exception {
        RemoteInstance remote = staticInstances.get(targeClass.getSimpleName());
        if (remote == null) {
            Object obj = targeClass.getDeclaredConstructor().newInstance();
            remote = new RemoteInstance(obj);
            staticInstances.put(targeClass.getSimpleName(), remote);
        }

        if (pooledId != null && !pooledId.equals(PoolingManager.NEW_POLLING)) {
            remote.setUUID(pooledId);
        }

        if (!isInjection) {
            context.setRequestId(remote.getUUID().toString());
            context.setInstance(remote.getInstance());
        }

        if (pooledId != null) {
            PoolingManager.addInstance(targeClass, remote.getUUID().toString());
        }

        return remote.getInstance();
    }

    private Object getPerRequestInstance(Class<?> targeClass, InvocationRequest context, boolean isInjection, String pooledId) throws Exception {
        RemoteInstance remote = perRequestInstances.get(pooledId == null ? context.getRequestId() : pooledId);
        if (remote == null) {
            Object obj = targeClass.getDeclaredConstructor().newInstance();
            remote = new RemoteInstance(obj);

            context.setRequestId(remote.getUUID().toString());
            perRequestInstances.put(context.getRequestId(), remote);
        }

        if (!isInjection) {
            context.setInstance(remote.getInstance());
        }

        if (pooledId != null) {
            PoolingManager.addInstance(targeClass, remote.getUUID().toString());
        }

        return remote.getInstance();
    }

    private Object getPerClientInstance(Class<?> targeClass, InvocationRequest context, boolean isInjection, String pooledId) throws Exception {
        Map<String, RemoteInstance> clientInstances = perClientInstances.get(context.getClientId());

        if (clientInstances == null) {
            clientInstances = new ConcurrentHashMap<>();
            perClientInstances.put(context.getClientId(), clientInstances);
        }

        RemoteInstance remote = clientInstances.get(targeClass.getSimpleName());
        if (remote == null) {
            Object obj = targeClass.getDeclaredConstructor().newInstance();
            remote = new RemoteInstance(obj);
            clientInstances.put(targeClass.getSimpleName(), remote);
        }

        if (pooledId != null && !pooledId.equals(PoolingManager.NEW_POLLING)) {
            remote.setUUID(pooledId);
        }

        if (!isInjection) {
            context.setRequestId(remote.getUUID().toString());
            context.setInstance(remote.getInstance());
        }

        if (pooledId != null) {
            PoolingManager.addInstance(targeClass, remote.getUUID().toString());
        }
        
        return remote.getInstance();
    }

    public void resolveInjection(Object instance, InvocationRequest context) throws Exception {
        Class<?> targetClass = instance.getClass();
        for (Field field : targetClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                Object dependency = getInstance(field.getType(), context, true);
                field.set(instance, dependency);
                field.setAccessible(false);
            }
        } 
    }
}
