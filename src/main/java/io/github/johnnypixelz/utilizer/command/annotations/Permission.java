package io.github.johnnypixelz.utilizer.command.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Repeatable(Permissions.class)
public @interface Permission {
    String value();

    String message() default "";

    String messageConfig() default "";

    String messagePath() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@interface Permissions {
    Permission[] value();
}
