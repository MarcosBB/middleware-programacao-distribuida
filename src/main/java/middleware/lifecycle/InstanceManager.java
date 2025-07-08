package middleware.lifecycle;


import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import middleware.annotations.Inject;
import middleware.annotations.InstanceScope;
import middleware.invoker.InvocationRequest;

public class InstanceManager {

    private Map<String, RemoteInstance> instances;

    private Map<String, String> staticInstancesIds;
    private Map<String, Map<String, String>> perClientInstancesIds;

    public InstanceManager() {
        this.instances = new ConcurrentHashMap<>();
        this.staticInstancesIds = new ConcurrentHashMap<>();
        this.perClientInstancesIds = new ConcurrentHashMap<>();
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
        String remoteId = staticInstancesIds.get(targeClass.getSimpleName());

        if (pooledId != null && !pooledId.equals(PoolingManager.NEW_POLLING)) {
            remoteId = pooledId;
        }

        RemoteInstance remote;
        if (remoteId == null) {
            remote = new RemoteInstance(targeClass);
            remoteId = remote.getUUIDStr();
            instances.put(remoteId, remote);
            staticInstancesIds.put(targeClass.getSimpleName(), remoteId);

            remote.setDestructionCallback(this);
        } else {
            remote = instances.get(remoteId);
        }

        if (!isInjection) {
            context.setRequestId(remoteId);
            context.setInstance(remote.getInstance());
        }

        if (pooledId != null) {
            PoolingManager.addInstance(targeClass, remoteId);
        }

        return remote.getInstance();
    }

    private Object getPerRequestInstance(Class<?> targeClass, InvocationRequest context, boolean isInjection, String pooledId) throws Exception {
        RemoteInstance remote = instances.get(pooledId == null ? context.getRequestId() : pooledId);
        if (remote == null) {
            remote = new RemoteInstance(targeClass);
            context.setRequestId(remote.getUUIDStr());
            instances.put(context.getRequestId(), remote);

            remote.setDestructionCallback(this);
        }

        if (!isInjection) {
            context.setInstance(remote.getInstance());
        }

        if (pooledId != null) {
            PoolingManager.addInstance(targeClass, remote.getUUIDStr());
        }

        return remote.getInstance();
    }

    private Object getPerClientInstance(Class<?> targeClass, InvocationRequest context, boolean isInjection, String pooledId) throws Exception {
        Map<String, String> clientInstances = perClientInstancesIds.get(context.getClientId());

        if (clientInstances == null) {
            clientInstances = new ConcurrentHashMap<>();
            perClientInstancesIds.put(context.getClientId(), clientInstances);
        }

        String remoteId = clientInstances.get(targeClass.getSimpleName());

        if (pooledId != null && !pooledId.equals(PoolingManager.NEW_POLLING)) {
            remoteId = pooledId;
        }

        RemoteInstance remote;
        if (remoteId == null) {
            remote = new RemoteInstance(targeClass);
            remoteId = remote.getUUIDStr();
            instances.put(remoteId, remote);
            clientInstances.put(targeClass.getSimpleName(), remoteId);

            remote.setDestructionCallback(this);
        } else {
            remote = instances.get(remoteId);
        }

        if (!isInjection) {
            context.setRequestId(remote.getUUIDStr());
            context.setInstance(remote.getInstance());
        }

        if (pooledId != null) {
            PoolingManager.addInstance(targeClass, remote.getUUIDStr());
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
    public void destroyInstance(String instanceId) {
        RemoteInstance instance = instances.get(instanceId);
        if (instance != null) {
            instances.remove(instanceId);
            staticInstancesIds.values().remove(instanceId);
            perClientInstancesIds.values().forEach(map -> map.values().remove(instanceId));
        }
    }
}
