package io.github.johnnypixelz.utilizer.sql.handlers;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementHandler {

    void handle(PreparedStatement preparedStatement) throws SQLException;

}
