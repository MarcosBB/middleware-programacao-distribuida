package middleware.lifecycle;

import java.util.UUID;

public class PerRequestInstanceManager implements InstanceManager {
    @Override
    public Object getInstance(Class<?> clazz) throws Exception {
        UUID uuid = UUID.randomUUID();

        try {
            return clazz.getDeclaredConstructor(UUID.class).newInstance(uuid);
        } catch (NoSuchMethodException e) {
            System.out.println("[PerRequestInstanceManager] Construtor com UUID não encontrado. Usando construtor padrão.");
            return clazz.getDeclaredConstructor().newInstance();
        }
    }
}
