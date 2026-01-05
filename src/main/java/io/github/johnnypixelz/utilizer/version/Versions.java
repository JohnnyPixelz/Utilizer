package io.github.johnnypixelz.utilizer.version;

import org.bukkit.Bukkit;

/**
 * Utility class for detecting and comparing Minecraft server versions.
 * Parses the version once on first access and caches the result.
 * <p>
 * Example usage:
 * <pre>
 * if (Versions.isAtLeast(1, 19, 4)) {
 *     // Use TextDisplay entities
 * } else {
 *     // Fall back to ArmorStand
 * }
 * </pre>
 */
public final class Versions {

    private static int major = -1;
    private static int minor = -1;
    private static int patch = -1;
    private static String versionString = null;

    private Versions() {
    }

    private static synchronized void init() {
        if (major != -1) return;

        try {
            // Bukkit.getBukkitVersion() returns format like "1.20.4-R0.1-SNAPSHOT"
            String bukkitVersion = Bukkit.getBukkitVersion();
            versionString = bukkitVersion.split("-")[0];

            String[] parts = versionString.split("\\.");
            major = Integer.parseInt(parts[0]);
            minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        } catch (Exception e) {
            major = 1;
            minor = 20;
            patch = 0;
            versionString = "1.20.0";
        }
    }

    // ===== Comparison Methods =====

    /**
     * Checks if the server version is at least the specified version.
     *
     * @param major Major version (e.g., 1)
     * @param minor Minor version (e.g., 19)
     * @return true if server version >= specified version
     */
    public static boolean isAtLeast(int major, int minor) {
        return isAtLeast(major, minor, 0);
    }

    /**
     * Checks if the server version is at least the specified version.
     *
     * @param major Major version (e.g., 1)
     * @param minor Minor version (e.g., 19)
     * @param patch Patch version (e.g., 4)
     * @return true if server version >= specified version
     */
    public static boolean isAtLeast(int major, int minor, int patch) {
        init();
        if (Versions.major != major) return Versions.major > major;
        if (Versions.minor != minor) return Versions.minor > minor;
        return Versions.patch >= patch;
    }

    /**
     * Checks if the server version is below the specified version.
     *
     * @param major Major version (e.g., 1)
     * @param minor Minor version (e.g., 19)
     * @return true if server version < specified version
     */
    public static boolean isBelow(int major, int minor) {
        return isBelow(major, minor, 0);
    }

    /**
     * Checks if the server version is below the specified version.
     *
     * @param major Major version (e.g., 1)
     * @param minor Minor version (e.g., 19)
     * @param patch Patch version (e.g., 4)
     * @return true if server version < specified version
     */
    public static boolean isBelow(int major, int minor, int patch) {
        return !isAtLeast(major, minor, patch);
    }

    /**
     * Checks if the server version is exactly the specified version.
     *
     * @param major Major version (e.g., 1)
     * @param minor Minor version (e.g., 20)
     * @param patch Patch version (e.g., 4)
     * @return true if server version == specified version
     */
    public static boolean isExactly(int major, int minor, int patch) {
        init();
        return Versions.major == major
                && Versions.minor == minor
                && Versions.patch == patch;
    }

    // ===== Getters =====

    /**
     * @return The major version number (e.g., 1 for "1.20.4")
     */
    public static int getMajor() {
        init();
        return major;
    }

    /**
     * @return The minor version number (e.g., 20 for "1.20.4")
     */
    public static int getMinor() {
        init();
        return minor;
    }

    /**
     * @return The patch version number (e.g., 4 for "1.20.4")
     */
    public static int getPatch() {
        init();
        return patch;
    }

    /**
     * @return The version string (e.g., "1.20.4")
     */
    public static String getVersionString() {
        init();
        return versionString;
    }

    // ===== Feature Detection Helpers =====

    /**
     * Checks if the server supports TextDisplay entities (1.19.4+).
     *
     * @return true if TextDisplay entities are available
     */
    public static boolean supportsTextDisplays() {
        return isAtLeast(1, 19, 4);
    }

    /**
     * Checks if the server supports Interaction entities (1.19.4+).
     *
     * @return true if Interaction entities are available
     */
    public static boolean supportsInteractionEntity() {
        return isAtLeast(1, 19, 4);
    }

    /**
     * Checks if the server supports the Adventure Component API (1.16+).
     *
     * @return true if Component API is available
     */
    public static boolean supportsComponents() {
        return isAtLeast(1, 16);
    }

    /**
     * Checks if the server supports PersistentDataContainer (1.14+).
     *
     * @return true if PDC is available
     */
    public static boolean supportsPersistentDataContainer() {
        return isAtLeast(1, 14);
    }

}
