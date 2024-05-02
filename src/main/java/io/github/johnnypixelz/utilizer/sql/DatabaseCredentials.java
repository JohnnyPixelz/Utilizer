package io.github.johnnypixelz.utilizer.sql;

import com.zaxxer.hikari.HikariConfig;
import io.github.johnnypixelz.utilizer.sql.drivers.SQLDriver;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.util.*;

public class DatabaseCredentials {

    @Nonnull
    public static DatabaseCredentials of(@Nonnull String address, int port, @Nonnull String database, @Nonnull String username, @Nonnull String password, @Nonnull SQLDriver driver) {
        return of(address, port, database, username, password, driver, new HashMap<>());
    }

    @Nonnull
    public static DatabaseCredentials of(@Nonnull String address, int port, @Nonnull String database, @Nonnull String username, @Nonnull String password, @Nonnull SQLDriver driver, @Nonnull Map<String, String> options) {
        return new DatabaseCredentials(address, port, database, username, password, driver, options);
    }

    @Nonnull
    public static DatabaseCredentials fromConfig(@Nonnull ConfigurationSection section, SQLDriver... supportedDrivers) {
        if (supportedDrivers.length == 0) {
            throw new IllegalArgumentException("No supported drivers found.");
        }

        Map<String, String> options = new HashMap<>();

        if (section.isConfigurationSection("properties")) {
            final ConfigurationSection propertiesSection = section.getConfigurationSection("properties");
            Objects.requireNonNull(propertiesSection);

            for (String property : propertiesSection.getKeys(false)) {
                options.put(property, propertiesSection.getString(property));
            }
        }

        final SQLDriver sqlDriver;

        final String mode = section.getString("mode");
        if (mode != null) {
            final Optional<SQLDriver> optionalSQLDriver = Arrays.stream(supportedDrivers)
                    .filter(driver -> driver.getName().equalsIgnoreCase(mode))
                    .findFirst();
            sqlDriver = optionalSQLDriver.orElseThrow(() -> new IllegalArgumentException("Mode " + mode + " is unsupported"));
        } else {
            sqlDriver = supportedDrivers[0];
        }

        return of(
                section.getString("address", "localhost"),
                section.getInt("port", 3306),
                section.getString("database", "minecraft"),
                section.getString("username", "root"),
                section.getString("password", "password"),
                sqlDriver,
                options
        );
    }

    private final String address;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final SQLDriver sqlDriver;
    private final Map<String, String> options;

    private DatabaseCredentials(@Nonnull String address, int port, @Nonnull String database, @Nonnull String username, @Nonnull String password, @Nonnull SQLDriver sqlDriver, @Nonnull Map<String, String> options) {
        this.address = Objects.requireNonNull(address);
        this.port = port;
        this.database = Objects.requireNonNull(database);
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        this.sqlDriver = Objects.requireNonNull(sqlDriver);
        this.options = Objects.requireNonNull(options);
    }

    @Nonnull
    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    @Nonnull
    public String getDatabase() {
        return this.database;
    }

    @Nonnull
    public String getUsername() {
        return this.username;
    }

    @Nonnull
    public String getPassword() {
        return this.password;
    }

    @Nonnull
    public SQLDriver getSqlDriver() {
        return sqlDriver;
    }

    @Nonnull
    public Map<String, String> getOptions() {
        return options;
    }

    @Nonnull
    public HikariConfig getHikariConfig() {
        return sqlDriver.generateHikariConfig(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof DatabaseCredentials other)) return false;

        return this.getAddress().equals(other.getAddress()) &&
                this.getPort() == other.getPort() &&
                this.getDatabase().equals(other.getDatabase()) &&
                this.getUsername().equals(other.getUsername()) &&
                this.getPassword().equals(other.getPassword());
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getPort();
        result = result * PRIME + this.getAddress().hashCode();
        result = result * PRIME + this.getDatabase().hashCode();
        result = result * PRIME + this.getUsername().hashCode();
        result = result * PRIME + this.getPassword().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DatabaseCredentials(" +
                "address=" + this.getAddress() + ", " +
                "port=" + this.getPort() + ", " +
                "database=" + this.getDatabase() + ", " +
                "username=" + this.getUsername() + ", " +
                "password=" + this.getPassword() + ")";
    }

}
