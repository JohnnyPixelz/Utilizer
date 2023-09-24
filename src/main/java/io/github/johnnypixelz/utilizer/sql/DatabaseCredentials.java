package io.github.johnnypixelz.utilizer.sql;

import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DatabaseCredentials {

    @Nonnull
    public static DatabaseCredentials of(@Nonnull String address, int port, @Nonnull String database, @Nonnull String username, @Nonnull String password) {
        return of(address, port, database, username, password, "mariadb", new HashMap<>());
    }

    @Nonnull
    public static DatabaseCredentials of(@Nonnull String address, int port, @Nonnull String database, @Nonnull String username, @Nonnull String password, @Nonnull String dataSource) {
        return of(address, port, database, username, password, dataSource, new HashMap<>());
    }

    @Nonnull
    public static DatabaseCredentials of(@Nonnull String address, int port, @Nonnull String database, @Nonnull String username, @Nonnull String password, @Nonnull String dataSource, @Nonnull Map<String, String> options) {
        return new DatabaseCredentials(address, port, database, username, password, dataSource, options);
    }

    @Nonnull
    public static DatabaseCredentials fromConfig(@Nonnull ConfigurationSection config) {
        Map<String, String> options = new HashMap<>();

        if (config.isConfigurationSection("properties")) {
            final ConfigurationSection section = config.getConfigurationSection("properties");
            Objects.requireNonNull(section);

            for (String property : section.getKeys(false)) {
                options.put(property, section.getString(property));
            }
        }

        return of(
                config.getString("address", "localhost"),
                config.getInt("port", 3306),
                config.getString("database", "minecraft"),
                config.getString("username", "root"),
                config.getString("password", "passw0rd"),
                config.getString("datasource", "mariadb"),
                options
        );
    }

    private final String address;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final String dataSource;
    private final Map<String, String> options;

    private DatabaseCredentials(@Nonnull String address, int port, @Nonnull String database, @Nonnull String username, @Nonnull String password, @Nonnull String dataSource, @Nonnull Map<String, String> options) {
        this.address = Objects.requireNonNull(address);
        this.port = port;
        this.database = Objects.requireNonNull(database);
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        this.dataSource = Objects.requireNonNull(dataSource);
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
    public String getDataSource() {
        return dataSource;
    }

    @Nonnull
    public Map<String, String> getOptions() {
        return options;
    }

    @Nonnull
    public String getJdbcURL() {
        String format = "jdbc:{source}://{address}:{port}/{database}?{options}";

        Map<String, String> options = new HashMap<>(getOptions());
        options.put("user", getUsername());
        options.put("password", getPassword());

        final String optionsString = options.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        return format.replace("{source}", dataSource)
                .replace("{address}", address)
                .replace("{port}", String.valueOf(port))
                .replace("{database}", database)
                .replace("{options}", optionsString);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof DatabaseCredentials)) return false;
        final DatabaseCredentials other = (DatabaseCredentials) o;

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
