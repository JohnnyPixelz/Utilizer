package io.github.johnnypixelz.utilizer.command.internal;

import io.github.johnnypixelz.utilizer.plugin.Logs;

/**
 * Centralized debug logging for the command system.
 * Disabled by default, can be enabled for troubleshooting.
 */
public final class DebugLogger {

    private static boolean enabled = false;

    private DebugLogger() {
    }

    /**
     * Enables or disables debug logging.
     *
     * @param enabled true to enable debug logging
     */
    public static void setEnabled(boolean enabled) {
        DebugLogger.enabled = enabled;
    }

    /**
     * @return true if debug logging is enabled
     */
    public static boolean isEnabled() {
        return enabled;
    }

    public static void commandRegistration(String label) {
        if (enabled) {
            Logs.info("[Command] Registering command: " + label);
        }
    }

    public static void commandExecution(String label, int argCount) {
        if (enabled) {
            Logs.info("[Command] Executing: " + label + " with " + argCount + " arguments");
        }
    }

    public static void subcommandSearch(String searching, String currentCommand) {
        if (enabled) {
            Logs.info("[Command] Searching for subcommand '" + searching + "' in '" + currentCommand + "'");
        }
    }

    public static void subcommandFound(String label) {
        if (enabled) {
            Logs.info("[Command] Found subcommand: " + label);
        }
    }

    public static void subcommandNotFound(String label, String currentCommand) {
        if (enabled) {
            Logs.info("[Command] Subcommand '" + label + "' not found in '" + currentCommand + "'");
        }
    }

    public static void methodParsing(String className, String methodName) {
        if (enabled) {
            Logs.info("[Command] Parsing method: " + className + "." + methodName);
        }
    }

    public static void annotationParsing(String className) {
        if (enabled) {
            Logs.info("[Command] Parsing annotations for: " + className);
        }
    }

    public static void tabComplete(String alias, String[] args) {
        if (enabled) {
            Logs.info("[Command] Tab completing: " + alias + " args: [" + String.join(", ", args) + "]");
        }
    }

    public static void argumentResolution(String parameterName, String argument, String resolvedType) {
        if (enabled) {
            Logs.info("[Command] Resolving argument '" + argument + "' for parameter '" + parameterName + "' to type " + resolvedType);
        }
    }

}
