package io.github.johnnypixelz.utilizer.sql;

import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * SQL class for opening SQL connections. Heavily inspired by lucko/helper library
 */
public class SQLClient {

    private static final AtomicInteger POOL_COUNTER = new AtomicInteger(0);

    // https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
    private static final int MAXIMUM_POOL_SIZE = (Runtime.getRuntime().availableProcessors() * 2) + 1;
    private static final int MINIMUM_IDLE = Math.min(MAXIMUM_POOL_SIZE, 10);

    private static final long MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30);
    private static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
    private static final long LEAK_DETECTION_THRESHOLD = TimeUnit.SECONDS.toMillis(10);

    HikariDataSource hikariDataSource;

    public SQLClient(DatabaseCredentials databaseCredentials) {
        final HikariConfig hikari = databaseCredentials.getHikariConfig();

        hikari.setPoolName("sql-" + POOL_COUNTER.getAndIncrement());

        hikari.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        hikari.setMinimumIdle(MINIMUM_IDLE);

        hikari.setMaxLifetime(MAX_LIFETIME);
        hikari.setConnectionTimeout(CONNECTION_TIMEOUT);
        hikari.setLeakDetectionThreshold(LEAK_DETECTION_THRESHOLD);

        Map<String, String> properties = ImmutableMap.<String, String>builder()
                // Ensure we use utf8 encoding
                .put("useUnicode", "true")
                .put("characterEncoding", "utf8")

                // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
                .put("cachePrepStmts", "true")
                .put("prepStmtCacheSize", "250")
                .put("prepStmtCacheSqlLimit", "2048")
                .put("useServerPrepStmts", "true")
                .put("useLocalSessionState", "true")
                .put("rewriteBatchedStatements", "true")
                .put("cacheResultSetMetadata", "true")
                .put("cacheServerConfiguration", "true")
                .put("elideSetAutoCommits", "true")
                .put("maintainTimeStats", "false")
                .put("alwaysSendSetIsolation", "false")
                .put("cacheCallableStmts", "true")

                // Set the driver level TCP socket timeout
                // See: https://github.com/brettwooldridge/HikariCP/wiki/Rapid-Recovery
                .put("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)))
                .build();

        for (Map.Entry<String, String> property : properties.entrySet()) {
            hikari.addDataSourceProperty(property.getKey(), property.getValue());
        }

        this.hikariDataSource = new HikariDataSource(hikari);
    }

    @Nonnull
    public HikariDataSource getHikari() {
        return hikariDataSource;
    }

    public Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    public void execute(@Nonnull String statement, @Nonnull Consumer<PreparedStatement> preparer) {
        try (Connection c = this.getConnection(); PreparedStatement s = c.prepareStatement(statement)) {
            preparer.accept(s);
            s.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <R> Optional<R> query(@Nonnull String query, @Nonnull Consumer<PreparedStatement> preparer, @Nonnull Function<ResultSet, R> handler) {
        try (Connection c = this.getConnection(); PreparedStatement s = c.prepareStatement(query)) {
            preparer.accept(s);
            try (ResultSet r = s.executeQuery()) {
                return Optional.ofNullable(handler.apply(r));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
