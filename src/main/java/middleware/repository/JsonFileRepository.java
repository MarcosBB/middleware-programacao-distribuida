package middleware.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import middleware.marshaller.JsonMarshaller;

public class JsonFileRepository implements DataRepository {

    public record RepoEntry(String className, String object) {}

    private final Path filePath;
    private final JsonMarshaller marshaller;
    private Map<String, RepoEntry> cache;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public JsonFileRepository() {
        marshaller = new JsonMarshaller();
        cache = new HashMap<>();

        String userHome = System.getProperty("user.home");
        // Criar um subdiretório para seus dados se desejar
        Path dataDir = Paths.get(userHome, ".myjavamiddleware", "data");
        try {
            Files.createDirectories(dataDir); // Garante que o diretório exista
        } catch (IOException e) {
            throw new RuntimeException("Falha ao criar diretório de dados: " + dataDir, e);
        }

        this.filePath = dataDir.resolve("repository.json");
    }

    @Override
    public void save(String id, Object obj) {
        // System.out.println("repo_save: " + obj.getClass().getSimpleName() + " | " + id);
        try {
            readFileToCache();
            RepoEntry entry = new RepoEntry(obj.getClass().getName(), marshaller.serialize(obj));
            cache.put(id, entry);
            saveCacheToFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Object> findById(String id) {
        readFileToCache();
        try {
            RepoEntry entry = cache.get(id);
            if(entry != null) {
                Class<?> targetClass = Class.forName(entry.className);
                Object obj = marshaller.deserialize(entry.object, targetClass);
                return Optional.of(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void delete(String id) {
        try {
            readFileToCache();
            cache.remove(id);
            saveCacheToFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean exists(String id) {
        readFileToCache();
        try {
            return cache.containsKey(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveCacheToFile() {
        lock.writeLock().lock();
        marshaller.saveToFile(filePath, cache);
        cache.clear();
        lock.writeLock().unlock();
    }

    private void readFileToCache() {
        lock.writeLock().lock();
        cache = marshaller.readFromFile(filePath);
        lock.writeLock().unlock();
    }

}
