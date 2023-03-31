package xyz.n7mn.dev.sqlite;

import xyz.n7mn.dev.sqlite.data.CasinoData;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CasinoDB {
    public CasinoDB() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            Statement statement = connection.createStatement();
            statement.executeUpdate("create table if not exists casino (guild string, userid string, coin integer)");
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

    public CasinoData get(String guild, String userId) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("select * from casino where guild = ? AND userid = ?");
            prepareStatement.setString(1, guild);
            prepareStatement.setString(2, userId);
            ResultSet resultSet = prepareStatement.executeQuery();
            if (resultSet.next())
                return new CasinoData(resultSet.getString(1), resultSet.getString(2), resultSet.getLong(3));

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

    public void insert(String guild, String userId, long coin) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("insert into casino values (?,?,?)");
            prepareStatement.setString(1, guild);
            prepareStatement.setString(2, userId);
            prepareStatement.setLong(3, coin);
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

    public void insert(String guild, String userId, String coin) {
        Connection connection = null;

        try {

            long newCoin = Long.parseLong(coin);

            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("insert into casino values (?,?,?)");
            prepareStatement.setString(1, guild);
            prepareStatement.setString(2, userId);
            prepareStatement.setLong(3, newCoin);
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

    public void update(String guild, String userId, long coin) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");
            PreparedStatement prepareStatement = connection.prepareStatement("update casino set coin = ? where guild = ? AND userid = ?");
            prepareStatement.setLong(1, coin);
            prepareStatement.setString(2, guild);
            prepareStatement.setString(3, userId);
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

    public void delete(String guild, String userid) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("delete from * casino where guild = ? AND userid = ?");
            prepareStatement.setString(1, guild);
            prepareStatement.setString(2, userid);
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

    public Map<String, CasinoData> getAll() {
        Map<String, CasinoData> temp = new HashMap<>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from casino");
            while (rs.next()) {
                temp.put(rs.getString("guild"), new CasinoData(rs.getString("guild"), rs.getString("userid"), rs.getLong("coin")));
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

    public List<CasinoData> getAll(String guild) {
        List<CasinoData> temp = new ArrayList<>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("select * from casino where guild = ?");
            prepareStatement.setString(1, guild);
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                temp.add(new CasinoData(resultSet.getString("guild"), resultSet.getString("userid"), resultSet.getLong("coin")));
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