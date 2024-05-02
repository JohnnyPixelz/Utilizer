package io.github.johnnypixelz.utilizer.sql.drivers;

public class MysqlDriver implements SQLDriver {

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public String getDataSourceClassName() {
        return "com.mysql.cj.jdbc.Driver";
    }

}
