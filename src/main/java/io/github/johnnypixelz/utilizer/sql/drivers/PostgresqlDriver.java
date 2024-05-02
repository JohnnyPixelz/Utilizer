package io.github.johnnypixelz.utilizer.sql.drivers;

import com.zaxxer.hikari.HikariConfig;
import io.github.johnnypixelz.utilizer.maven.Dependency;
import io.github.johnnypixelz.utilizer.maven.DependencyLoader;
import io.github.johnnypixelz.utilizer.sql.DatabaseCredentials;

public class PostgresqlDriver implements SQLDriver {

    @Override
    public String getName() {
        return "postgres";
    }

    @Override
    public String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    public String getDataSourceClassName() {
        return "org.postgresql.ds.PGSimpleDataSource";
    }

    @Override
    public Dependency getDriverDependency() {
        return Dependency.of("org.postgresql", "postgresql", "42.7.3");
    }

    @Override
    public HikariConfig generateHikariConfig(DatabaseCredentials databaseCredentials) {
        final HikariConfig hikariConfig = new HikariConfig();

        DependencyLoader.load(getDriverDependency());
        hikariConfig.setDataSourceClassName(getDataSourceClassName());

        hikariConfig.addDataSourceProperty("serverName", databaseCredentials.getAddress());
        hikariConfig.addDataSourceProperty("portNumber", String.valueOf(databaseCredentials.getPort()));
        hikariConfig.addDataSourceProperty("databaseName", databaseCredentials.getDatabase());
        hikariConfig.addDataSourceProperty("user", databaseCredentials.getUsername());
        hikariConfig.addDataSourceProperty("password", databaseCredentials.getPassword());

        databaseCredentials.getOptions().forEach(hikariConfig::addDataSourceProperty);

        return hikariConfig;
    }

}
