package io.github.johnnypixelz.utilizer.sql.drivers;

import com.zaxxer.hikari.HikariConfig;
import io.github.johnnypixelz.utilizer.maven.Dependency;
import io.github.johnnypixelz.utilizer.maven.DependencyLoader;
import io.github.johnnypixelz.utilizer.sql.DatabaseCredentials;

public class MysqlDriver implements SQLDriver {

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public String getDriverClassName() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    public String getDataSourceClassName() {
        return "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"; // Known to be broken https://github.com/brettwooldridge/HikariCP?tab=readme-ov-file#popular-datasource-class-names
    }

    @Override
    public Dependency getDriverDependency() {
        return Dependency.of("mysql", "mysql-connector-java", "8.0.33");
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
