package io.github.johnnypixelz.utilizer.plugin.ap;

/**
 * Represents a plugin dependency
 */
public @interface PluginDependency {

    /**
     * The name of the plugin
     *
     * @return the name of the plugin
     */

    String value();

    /**
     * If this is a "soft" dependency
     *
     * @return true if this is a "soft" dependency
     */
    boolean soft() default false;

}
