package middleware.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InstanceScope {
    ScopeType value();

    public enum ScopeType {
        STATIC,
        PER_REQUEST,
        PER_CLIENT
    }

}