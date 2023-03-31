package xyz.n7mn.dev.sqlite;

import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EarthQuakeDB {
    public EarthQuakeDB() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            Statement statement = connection.createStatement();
            statement.executeUpdate("create table if not exists earthquake (guild string, channel string, realtime bool, yahoo bool)");
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

    public EarthQuakeData get(String guild) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("select * from earthquake where guild = ?");
            prepareStatement.setString(1, guild);
            ResultSet resultSet = prepareStatement.executeQuery();
            if (resultSet.next()) {
                return new EarthQuakeData(resultSet.getString(1), resultSet.getString(2), resultSet.getBoolean(3), resultSet.getBoolean(4));
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
        return null;
    }

    public void insert(String guild, String channel) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("insert into earthquake values (?,?,?,?)");
            prepareStatement.setString(1, guild);
            prepareStatement.setString(2, channel);
            prepareStatement.setBoolean(3, true);
            prepareStatement.setBoolean(4, true);
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

    public void update(String guild, String channel, boolean announceRealTime, boolean announceInfo) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("update earthquake set channel = ? where guild = ?");
            prepareStatement.setString(1, channel);
            prepareStatement.setString(2, guild);
            prepareStatement.setBoolean(3, announceRealTime);
            prepareStatement.setBoolean(4, announceInfo);
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

            PreparedStatement prepareStatement = connection.prepareStatement("delete from earthquake where guild = ?");
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

    public List<EarthQuakeData> getAll() {
        List<EarthQuakeData> earthQuake = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from earthquake");
            while (rs.next()) {
                earthQuake.add(new EarthQuakeData(rs.getString(1), rs.getString(2), rs.getBoolean(4), rs.getBoolean(4)));
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
        return earthQuake;
    }

    public Map<String, String> getLegacyAll() {
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

    @Getter
    @Setter
    public static class EarthQuakeData {
        private String guild, channel;
        private boolean announceRealTime, announceInfo;

        public EarthQuakeData(String guild, String channel, boolean announceRealTime, boolean announceInfo) {
            this.guild = guild;
            this.channel = channel;
            this.announceRealTime = announceRealTime;
            this.announceInfo = announceInfo;
        }

        public void update() {
            SQLite.INSTANCE.getEarthQuake().update(guild, channel, announceRealTime, announceInfo);
        }

        public void remove() {
            SQLite.INSTANCE.getEarthQuake().delete(guild);
        }
    }
}