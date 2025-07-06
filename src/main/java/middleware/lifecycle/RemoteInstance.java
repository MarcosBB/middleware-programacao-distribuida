package middleware.lifecycle;

import java.util.UUID;

public class RemoteInstance {

    private UUID instanceUUID;
    private Object instance;

    public RemoteInstance(Object instance) {
        this.instanceUUID = UUID.randomUUID();
        this.instance = instance;
    }

    public UUID getUUID() {
        return instanceUUID;
    }

    public void setUUID(UUID uuid) {
        this.instanceUUID = uuid;
    }

    public Object getInstance() {
        return instance;
    }

    public Class<?> getInstanceClass() {
        return instance.getClass();
    }

}
