package middleware.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RemoteMethod {
    String name();
    RequestType requestType(); // http request type, e.g., "GET", "POST"

    public enum RequestType {
        GET,
        POST,
        PUT,
        DELETE
    }
}
