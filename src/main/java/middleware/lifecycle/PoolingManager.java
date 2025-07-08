package middleware.lifecycle;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import middleware.annotations.Poolable;

public class PoolingManager {

    private static Map<String, Queue<String>> poolList;
    public static final String NEW_POLLING = "NEW_POOLING";

    private static void startPooling(){
        if (poolList == null) {
            poolList = new ConcurrentHashMap<>();
        }
    }

    public static void addInstance(Class<?> targetClass, String instanceId) throws Exception {
        startPooling();
        
        if(!targetClass.isAnnotationPresent(Poolable.class)) {
            return;
        }

        String className = targetClass.getSimpleName();
        Poolable annot = targetClass.getAnnotation(Poolable.class);
        int poolSize = annot.poolSize();

        if (!poolList.containsKey(className)) {
            poolList.put(className, new ConcurrentLinkedQueue<>());
        }

        Queue<String> pool = poolList.get(className);

        if (pool.size() >= poolSize) {
            throw new IllegalStateException("Pool size exceeded for class: " + className);
        }

        pool.add(instanceId);
    }

    public static String poolInstance(Class<?> targetClass) {
        startPooling();
        
        if(!targetClass.isAnnotationPresent(Poolable.class)) {
            return null;
        }

        String className = targetClass.getSimpleName();
        Poolable annot = targetClass.getAnnotation(Poolable.class);
        int poolSize = annot.poolSize();

        if (!poolList.containsKey(className)) {
            poolList.put(className, new ConcurrentLinkedQueue<>());
        }

        Queue<String> pool = poolList.get(className);

        if (pool.size() >= poolSize) {
            return pool.poll();
        }

        return NEW_POLLING;
    }


}
