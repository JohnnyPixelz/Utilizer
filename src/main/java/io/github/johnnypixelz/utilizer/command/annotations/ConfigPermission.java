package io.github.johnnypixelz.utilizer.command.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Repeatable(ConfigPermissions.class)
public @interface ConfigPermission {
    String config();

    String path();

    String message() default "";

    String messageConfig() default "";

    String messagePath() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@interface ConfigPermissions {
    ConfigPermission[] value();
}
