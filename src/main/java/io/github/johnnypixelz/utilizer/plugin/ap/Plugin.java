package io.github.johnnypixelz.utilizer.plugin.ap;
import org.bukkit.plugin.PluginLoadOrder;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to automatically generate plugin.yml files for helper projects
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Plugin {

    /**
     * The name of the plugin
     *
     * @return the name of the plugin
     */
    String name();

    /**
     * The plugin version
     *
     * @return the plugin version
     */
    String version() default "";

    /**
     * A description of the plugin
     *
     * @return a description of the plugin
     */
    
    String description() default "";

    /**
     * The load order of the plugin
     *
     * @return the load order of the plugin
     */
    
    PluginLoadOrder load() default PluginLoadOrder.POSTWORLD;

    /**
     * The api version of the plugin
     *
     * @return the api version of the plugin
     */
    String apiVersion() default "";

    /**
     * The authors of the plugin
     *
     * @return the author of the plugin
     */
    
    String[] authors() default {};

    /**
     * A website for the plugin
     *
     * @return a website for the plugin
     */
    
    String website() default "";

    /**
     * A list of dependencies for the plugin
     *
     * @return a list of dependencies for the plugin
     */
    
    PluginDependency[] depends() default {};

    /**
     * A list of hard dependencies for the plugin
     *
     * @return a list of hard dependencies for the plugin
     */
    
    String[] hardDepends() default {};

    /**
     * A list of soft dependencies for the plugin
     *
     * @return a list of soft dependencies for the plugin
     */
    
    String[] softDepends() default {};

    /**
     * A list of plugins which should be loaded before this plugin
     *
     * @return a list of plugins which should be loaded before this plugin
     */
    
    String[] loadBefore() default {};

}
