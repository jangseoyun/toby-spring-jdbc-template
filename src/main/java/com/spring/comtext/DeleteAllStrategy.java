package com.spring.comtext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteAllStrategy implements StatementStrategy{

    @Override
    public PreparedStatement makePreparedStatement(Connection connection) throws SQLException {
        return null;
    }
}
