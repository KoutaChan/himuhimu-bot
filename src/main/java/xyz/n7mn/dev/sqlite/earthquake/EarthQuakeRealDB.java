package xyz.n7mn.dev.sqlite.earthquake;

import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class EarthQuakeRealDB extends EarthQuakeConnection {
    @SneakyThrows
    public EarthQuakeRealDB() {
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS REALTIME (LONG GUILD, LONG CHANNEL, INT LEVEL, BOOLEAN ALERT)");
    }

    @SneakyThrows
    public EarthQuakeData get(long guild) {
        PreparedStatement prepareStatement = connection.prepareStatement("SELECT * FROM REALTIME WHERE GUILD = ?");
        prepareStatement.setLong(1, guild);
        ResultSet resultSet = prepareStatement.executeQuery();
        if (resultSet.next()) {
            return new EarthQuakeRealData(this, resultSet.getLong(1), resultSet.getLong(2), resultSet.getInt(3), resultSet.getBoolean(4));
        }
        return null;
    }

    @SneakyThrows
    public void update(EarthQuakeData data) {
        PreparedStatement prepareStatement = connection.prepareStatement("UPDATE REALTIME SET CHANNEL = ?, LEVEL = ?, ALERT = ? WHERE GUILD = ?");
        prepareStatement.setLong(1, data.getChannel());
        prepareStatement.setInt(2, data.getLevel());
        prepareStatement.setBoolean(3, data.isAlert());
        prepareStatement.executeUpdate();
    }

    @SneakyThrows
    public void delete(long guild) {
        PreparedStatement prepareStatement = connection.prepareStatement("DELETE FROM REALTIME WHERE GUILD = ?");
        prepareStatement.setLong(1, guild);
        prepareStatement.executeUpdate();
    }

    public void delete(EarthQuakeData data) {
        delete(data.getGuild());
    }
}