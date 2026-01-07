package io.github.johnnypixelz.utilizer.sql.drivers;

import com.zaxxer.hikari.HikariConfig;
import io.github.johnnypixelz.utilizer.maven.Dependency;
import io.github.johnnypixelz.utilizer.sql.DatabaseCredentials;

import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public interface SQLDriver {

    String getName();

    String getDriverClassName();

    String getDataSourceClassName();

    Dependency getDriverDependency();

    default String getJdbcUrlSchema() {
        return "jdbc:{source}://{address}:{port}/{database}";
    }

    default String getJdbcUrl(String address, String port, String user, String password, String database, @Nullable Map<String, String> options) {
        final Map<String, String> optionsMap;
        if (options != null) {
            optionsMap = new HashMap<>(options);
        } else {
            optionsMap = new HashMap<>();
        }

        optionsMap.put("user", user);
        optionsMap.put("password", password);

        String optionsString = optionsMap.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        optionsString = "?" + optionsString;

        return getJdbcUrlSchema()
                .replace("{source}", getName())
                .replace("{address}", address)
                .replace("{port}", port)
                .replace("{database}", database)
                + optionsString;
    }

    HikariConfig generateHikariConfig(DatabaseCredentials databaseCredentials);

}
