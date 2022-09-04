package xyz.n7mn.dev.sqlite;

import xyz.n7mn.dev.sqlite.data.SQLiteMusicData;

import java.sql.*;

public class MusicDB {
    public MusicDB() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            Statement statement = connection.createStatement();
            statement.executeUpdate("create table if not exists music (guild string, defaultVolume integer, maxVolume integer)");
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

    public SQLiteMusicData get(String guild) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("select * from music where guild = ?");
            prepareStatement.setString(1, guild);
            ResultSet resultSet = prepareStatement.executeQuery();
            if (resultSet.next())
                return new SQLiteMusicData(resultSet.getString(1), resultSet.getInt(2), resultSet.getInt(3));

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

    public void insert(String guild, int defaultVolume, int maxVolume) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("insert into music values (?,?,?)");
            prepareStatement.setString(1, guild);
            prepareStatement.setInt(2, defaultVolume);
            prepareStatement.setInt(3, maxVolume);
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

    public void updateMaxVolume(String guild, int maxVolume) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("update music set maxVolume = ? where guild = ?");
            prepareStatement.setInt(1, maxVolume);
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

    public void updateDefaultVolume(String guild, int defaultVolume) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:botdata.db");

            PreparedStatement prepareStatement = connection.prepareStatement("update music set defaultVolume = ? where guild = ?");
            prepareStatement.setInt(1, defaultVolume);
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

            PreparedStatement prepareStatement = connection.prepareStatement("delete from * music where guild = ?");
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
}