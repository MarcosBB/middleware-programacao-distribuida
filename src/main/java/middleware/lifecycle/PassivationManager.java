package middleware.lifecycle;

import java.util.Optional;

import middleware.annotations.Passivable;
import middleware.repository.DataRepository;
import middleware.repository.JsonFileRepository;

public class PassivationManager {

    private static DataRepository repository = new JsonFileRepository();

    public static void save(String id, Object instance) throws Exception {
        try {
            Class<?> targetClass = instance.getClass();
            if (targetClass.isAnnotationPresent(Passivable.class)) {
                repository.save(id, instance);
            }    
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static Object findById(String id) throws Exception {
        Optional<Object> opt = repository.findById(id);
        return opt.orElseGet(() -> {return null;});
    }

}
