package io.github.johnnypixelz.utilizer.sql.v2.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    String name();
    String type(); // Example: "VARCHAR(36)", "DOUBLE"
    boolean primaryKey() default false;
}
