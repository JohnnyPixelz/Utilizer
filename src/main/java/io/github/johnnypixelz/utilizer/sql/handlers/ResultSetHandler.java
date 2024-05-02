package io.github.johnnypixelz.utilizer.sql.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetHandler<R> {

    R handle(ResultSet resultSet) throws SQLException;

}
