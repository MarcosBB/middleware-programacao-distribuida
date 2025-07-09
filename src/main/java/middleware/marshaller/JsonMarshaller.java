package middleware.marshaller;

import java.nio.file.Path;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import middleware.repository.JsonFileRepository;

public class JsonMarshaller {

    private final ObjectMapper mapper;

    public JsonMarshaller() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public <T> T deserialize(String json, Class<T> clazz) throws Exception {
        return mapper.readValue(json, clazz);
    }

    public String serialize(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }

    public void saveToFile(Path filePath, Map<String, JsonFileRepository.RepoEntry> entries) {
        try {
            mapper.writeValue(filePath.toFile(), entries);
            System.out.println(filePath.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, JsonFileRepository.RepoEntry> readFromFile(Path filePath) {
        try {
            return mapper.readValue(filePath.toFile(), new TypeReference<Map<String, JsonFileRepository.RepoEntry>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
