package middleware.lifecycle;

import java.util.UUID;

import middleware.annotations.Leasable;

public class RemoteInstance {

    private UUID instanceUUID;
    private Object instance;

    private long lastAccessedTime;
    private long leaseDuration;
    public Runnable destructionCallback;

    public RemoteInstance(Class<?> targetClass) throws Exception {
        this.instanceUUID = UUID.randomUUID();
        this.create(targetClass);
        this.verifyLease();
    }

    public RemoteInstance(Object instance) {
        this.instanceUUID = UUID.randomUUID();
        this.instance = instance;
        this.verifyLease();
    }

    public RemoteInstance(Object instance, String uuid) {
        this.instance = instance;
        this.setUUID(uuid);
        this.verifyLease();
    }

    public UUID getUUID() {
        return instanceUUID;
    }

    public String getUUIDStr() {
        return instanceUUID.toString();
    }

    public void setUUID(UUID uuid) {
        this.instanceUUID = uuid;
    }

    public void setUUID(String uuid) {
        this.instanceUUID = UUID.fromString(uuid);
    }

    public Object getInstance() {
        updateLease();
        return instance;
    }

    public Class<?> getInstanceClass() {
        return instance.getClass();
    }

    private void create(Class<?> targetClass) throws Exception {
        this.instance = targetClass.getDeclaredConstructor().newInstance();
    }

    public void destroy() {
        if (destructionCallback != null) {
            destructionCallback.run();
        }
        this.instance = null;
    }

    private void verifyLease() {
        Class<?> targetClass = instance.getClass();
        if (targetClass.isAnnotationPresent(Leasable.class)) {
            Leasable leasable = targetClass.getAnnotation(Leasable.class);
            this.leaseDuration = leasable.leaseTime();
            this.lastAccessedTime = System.nanoTime();
            LeasingManager.registerLeasableInstance(this);
        } else {
            this.leaseDuration = 0;
            this.lastAccessedTime = Long.MAX_VALUE;
        }
    }

    private void updateLease() {
        if (this.leaseDuration > 0) {
            this.lastAccessedTime = System.nanoTime();
        }
    }

    public boolean isLeaseExpired() {
        if (leaseDuration <= 0) { // Não é leasable
            return false;
        }
        return System.currentTimeMillis() - lastAccessedTime > leaseDuration;
    }

}
