package xyz.n7mn.dev.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteUtils {
    public static Connection createConnection(String name) {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
