package middleware.lifecycle;

import java.util.UUID;

public class RemoteInstance {

    private UUID instanceUUID;
    private Object instance;

    public RemoteInstance(Object instance) {
        this.instanceUUID = UUID.randomUUID();
        this.instance = instance;
    }

    public RemoteInstance(Object instance, String uuid) {
        this.instance = instance;
        this.setUUID(uuid);
    }

    public UUID getUUID() {
        return instanceUUID;
    }

    public void setUUID(UUID uuid) {
        this.instanceUUID = uuid;
    }

    public void setUUID(String uuid) {
        this.instanceUUID = UUID.fromString(uuid);
    }

    public Object getInstance() {
        return instance;
    }

    public Class<?> getInstanceClass() {
        return instance.getClass();
    }

}
