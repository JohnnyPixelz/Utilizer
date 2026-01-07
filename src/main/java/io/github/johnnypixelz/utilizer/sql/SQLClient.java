package io.github.johnnypixelz.utilizer.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.johnnypixelz.utilizer.sql.handlers.PreparedStatementHandler;
import io.github.johnnypixelz.utilizer.sql.handlers.ResultSetHandler;
import io.github.johnnypixelz.utilizer.tasks.Tasks;

import org.jetbrains.annotations.NotNull;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SQL class for opening SQL connections. Heavily inspired by lucko/helper library
 */
public class SQLClient implements Closeable {

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

        this.hikariDataSource = new HikariDataSource(hikari);
    }

    @NotNull
    public HikariDataSource getHikari() {
        return hikariDataSource;
    }

    public Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    public void execute(@NotNull String statement) {
        try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void execute(@NotNull String statement, @NotNull PreparedStatementHandler preparer) {
        try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparer.handle(preparedStatement);
            preparedStatement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void executeAsync(@NotNull String statement) {
        Tasks.async().run(() -> execute(statement));
    }

    public void executeAsync(@NotNull String statement, @NotNull PreparedStatementHandler preparer) {
        Tasks.async().run(() -> execute(statement, preparer));
    }

    public <R> Optional<R> executeQuery(@NotNull String query, @NotNull ResultSetHandler<R> handler) {
        try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return Optional.ofNullable(handler.handle(resultSet));
            } catch (SQLException sqlException) {
                return Optional.empty();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return Optional.empty();
        }
    }

    public <R> Optional<R> executeQuery(@NotNull String query, @NotNull PreparedStatementHandler preparer, @NotNull ResultSetHandler<R> handler) {
        try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparer.handle(preparedStatement);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return Optional.ofNullable(handler.handle(resultSet));
            } catch (SQLException sqlException) {
                return Optional.empty();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void close() {
        hikariDataSource.close();
    }

}
