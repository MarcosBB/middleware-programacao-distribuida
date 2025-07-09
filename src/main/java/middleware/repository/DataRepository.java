package middleware.repository;

import java.util.Optional;

public interface DataRepository {

    void save(String id, Object obj);
    Optional<Object> findById(String id);
    void delete(String id);
    boolean exists(String id);

}
