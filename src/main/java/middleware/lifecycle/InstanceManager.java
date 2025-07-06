package middleware.lifecycle;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
        InstanceScope scope = targeClass.getAnnotation(InstanceScope.class);
        
        if (scope == null) {
            throw new Exception("Class " + targeClass.getName() + " does not have InstanceScope annotation.");
        }

        if (context.getClientId() == null || context.getClientId().isEmpty()) {
            context.setClientId(UUID.randomUUID().toString());
        }

        if (context.getRequestId() == null || context.getRequestId().isEmpty()) {
            context.setRequestId("");
        }

        switch (scope.value()) {
            case STATIC:
                RemoteInstance remoteStatic = staticInstances.get(targeClass.getSimpleName());
                if (remoteStatic == null) {
                    Object obj = targeClass.getDeclaredConstructor().newInstance();
                    remoteStatic = new RemoteInstance(obj);
                    staticInstances.put(targeClass.getSimpleName(), remoteStatic);
                }

                if (!isInjection) {
                    context.setRequestId(remoteStatic.getUUID().toString());
                    context.setInstance(remoteStatic.getInstance());
                }

                return remoteStatic.getInstance();
            case PER_REQUEST:
                RemoteInstance remotePerReq = perRequestInstances.get(context.getRequestId());
                if (remotePerReq == null) {
                    Object obj = targeClass.getDeclaredConstructor().newInstance();
                    remotePerReq = new RemoteInstance(obj);
                    staticInstances.put(targeClass.getSimpleName(), remotePerReq);
                }

                if (!isInjection) {
                    context.setRequestId(remotePerReq.getUUID().toString());
                    context.setInstance(remotePerReq.getInstance());
                }
                
                return remotePerReq.getInstance();
            case PER_CLIENT:
                Map<String, RemoteInstance> clientInstances = perClientInstances.get(context.getClientId());
                if (clientInstances == null) {
                    clientInstances = new ConcurrentHashMap<>();
                    perClientInstances.put(context.getClientId(), clientInstances);
                }

                RemoteInstance remotePerCli = clientInstances.get(targeClass.getSimpleName());
                if (remotePerCli == null) {
                    Object obj = targeClass.getDeclaredConstructor().newInstance();
                    remotePerReq = new RemoteInstance(obj);
                    staticInstances.put(targeClass.getSimpleName(), remotePerReq);
                }

                if (!isInjection) {
                    context.setRequestId(remotePerCli.getUUID().toString());
                    context.setInstance(remotePerCli.getInstance());
                }
                
                return remotePerCli.getInstance();
            default:
                return null;
        }
    }

    public void resolveInjection(Object instance, InvocationRequest context) throws Exception {
        // Implement logic to resolve injection for the given instance
        // This could involve checking for annotations and injecting dependencies accordingly
    }

}
