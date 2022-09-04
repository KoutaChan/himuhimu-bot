package xyz.n7mn.dev.sqlite;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class EarthQuakeDB {

    public EarthQuakeDB() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            Statement statement = connection.createStatement();
            statement.executeUpdate("create table if not exists earthquake (guild string, channel string)");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String[] get(String guild) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("select * from earthquake where guild = ?");
            prepareStatement.setString(1, guild);
            ResultSet resultSet = prepareStatement.executeQuery();
            if (resultSet.next()) return new String[]{resultSet.getString(1), resultSet.getString(2)};

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void insert(String guild, String channel) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("insert into earthquake values (?,?)");
            prepareStatement.setString(1, guild);
            prepareStatement.setString(2, channel);
            prepareStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(String guild, String channel) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("update earthquake set channel = ? where guild = ?");
            prepareStatement.setString(1, channel);
            prepareStatement.setString(2, guild);
            prepareStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete(String guild) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("delete from * earthquake where guild = ?");
            prepareStatement.setString(1, guild);
            prepareStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, String> getAll() {
        Map<String, String> temp = new HashMap<>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from earthquake");
            while (rs.next()) {
                temp.put(rs.getString("guild"), rs.getString("channel"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return temp;
    }
}