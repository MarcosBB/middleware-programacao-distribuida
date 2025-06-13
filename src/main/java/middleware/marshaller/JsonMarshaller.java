package middleware.marshaller;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMarshaller {

    private final ObjectMapper mapper = new ObjectMapper();

    public <T> T deserialize(String json, Class<T> clazz) throws Exception {
        return mapper.readValue(json, clazz);
    }

    public String serialize(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }
}
