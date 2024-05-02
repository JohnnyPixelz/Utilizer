package io.github.johnnypixelz.utilizer.sql.drivers;

import com.zaxxer.hikari.HikariConfig;
import io.github.johnnypixelz.utilizer.maven.Dependency;
import io.github.johnnypixelz.utilizer.maven.DependencyLoader;
import io.github.johnnypixelz.utilizer.sql.DatabaseCredentials;

public class MariaDBDriver implements SQLDriver {

    @Override
    public String getName() {
        return "mariadb";
    }

    @Override
    public String getDriverClassName() {
        return "org.mariadb.jdbc.Driver";
    }

    @Override
    public String getDataSourceClassName() {
        return "org.mariadb.jdbc.MariaDbDataSource";
    }

    @Override
    public Dependency getDriverDependency() {
        return Dependency.of("org.mariadb.jdbc", "mariadb-java-client", "3.3.3");
    }

    @Override
    public HikariConfig generateHikariConfig(DatabaseCredentials databaseCredentials) {
        final HikariConfig hikariConfig = new HikariConfig();

        DependencyLoader.load(getDriverDependency());
        hikariConfig.setDataSourceClassName(getDataSourceClassName());

        final String jdbc = getJdbcUrl(
                databaseCredentials.getAddress(),
                String.valueOf(databaseCredentials.getPort()),
                databaseCredentials.getUsername(),
                databaseCredentials.getPassword(),
                databaseCredentials.getDatabase(),
                databaseCredentials.getOptions()
        );

        hikariConfig.addDataSourceProperty("url", jdbc);

        hikariConfig.setUsername(databaseCredentials.getUsername());
        hikariConfig.setPassword(databaseCredentials.getPassword());

        return hikariConfig;
    }

}
