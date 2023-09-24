package io.github.johnnypixelz.utilizer.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.johnnypixelz.utilizer.maven.Dependency;
import io.github.johnnypixelz.utilizer.maven.DependencyLoader;
import io.github.johnnypixelz.utilizer.plugin.Provider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.mariadb.jdbc.Driver;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * SQL class for opening SQL connections. Heavily inspired by lucko/helper library
 */
public class SQL {
    private static final AtomicInteger POOL_COUNTER = new AtomicInteger(0);

    // https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
    private static final int MAXIMUM_POOL_SIZE = (Runtime.getRuntime().availableProcessors() * 2) + 1;
    private static final int MINIMUM_IDLE = Math.min(MAXIMUM_POOL_SIZE, 2);

    private static final long MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30);
    private static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
    private static final long LEAK_DETECTION_THRESHOLD = TimeUnit.SECONDS.toMillis(10);

    public static SQL connect(@Nonnull DatabaseCredentials credentials) {
        final String dataSource = credentials.getDataSource();

        SQLDialect dialect;

        try {
            dialect = SQLDialect.valueOf(dataSource.toUpperCase());
        } catch (IllegalArgumentException ex) {
            dialect = SQLDialect.MARIADB;
        }

        return new SQL(credentials, dialect);
    }

    public static SQL connect(@Nonnull DatabaseCredentials credentials, @Nonnull SQLDialect dialect) {
        return new SQL(credentials, dialect);
    }

    private final HikariDataSource source;
    private final DatabaseCredentials credentials;
    private final DSLContext dsl;

    private SQL(@Nonnull DatabaseCredentials credentials, @Nonnull SQLDialect dialect) {
        this.credentials = credentials;
        final HikariConfig hikari = new HikariConfig();

        switch (credentials.getDataSource().toLowerCase()) {
            case "postgresql" -> {
                DependencyLoader.load(Dependency.of("org.postgresql", "postgresql", "42.2.10"));
//                hikari.setDriverClassName(PGSimpleDataSource.class.getName());
            }
            case "mariadb" -> {
                DependencyLoader.load(Dependency.of("org.mariadb.jdbc", "mariadb-java-client", "2.7.0"));
                hikari.setDriverClassName(Driver.class.getName());
            }
            default -> throw new IllegalArgumentException("Unsupported data source");
        }

        hikari.setJdbcUrl(credentials.getJdbcURL());
        Provider.getPlugin().getLogger().info(hikari.getJdbcUrl());

        hikari.setPoolName("sql-" + POOL_COUNTER.getAndIncrement());

//        hikari.addDataSourceProperty("serverName", credentials.getAddress());
//        hikari.addDataSourceProperty("databaseName", credentials.getDatabase());

//        hikari.setUsername(credentials.getUsername());
//        hikari.setPassword(credentials.getPassword());

//        credentials.getProperties().forEach(hikari::addDataSourceProperty);

        hikari.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        hikari.setMinimumIdle(MINIMUM_IDLE);

        hikari.setMaxLifetime(MAX_LIFETIME);
        hikari.setConnectionTimeout(CONNECTION_TIMEOUT);
        hikari.setLeakDetectionThreshold(LEAK_DETECTION_THRESHOLD);


        this.source = new HikariDataSource(hikari);
        this.dsl = DSL.using(source, dialect);
    }

    @Nonnull
    public HikariDataSource getHikari() {
        return this.source;
    }

    @Nonnull
    public DatabaseCredentials getCredentials() {
        return credentials;
    }

    @Nonnull
    public Connection getConnection() throws SQLException {
        return Objects.requireNonNull(dsl.parsingConnection(), "connection is null");
    }

    public <R> Optional<R> query(@Nonnull Function<DSLContext, R> query) {
        final R result = query.apply(dsl);
        return Optional.ofNullable(result);
    }

    public void execute(@Nonnull Consumer<DSLContext> execute) {
        execute.accept(dsl);
    }

    public void close() {
        this.source.close();
    }

}
