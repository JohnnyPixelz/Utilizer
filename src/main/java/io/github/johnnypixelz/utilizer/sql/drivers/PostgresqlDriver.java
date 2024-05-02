package io.github.johnnypixelz.utilizer.sql.drivers;

public class PostgresqlDriver implements SQLDriver {

    @Override
    public String getName() {
        return "postgres";
    }

    @Override
    public String getDataSourceClassName() {
        return "org.postgresql.ds.PGSimpleDataSource";
    }

}
