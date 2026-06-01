package dataaccess;

import com.google.gson.Gson;
import model.DataAccessException;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import static dataaccess.DatabaseManager.executeUpdate;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL UNIQUE,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) DEFAULT NULL UNIQUE,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`)
            )
            """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {
        var statement = "TRUNCATE TABLE users";
        executeUpdate(statement);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var json = rs.getString("json");
                        return new Gson().fromJson(json, UserData.class);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Please enter valid username and password.");
        }
        return null;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email, json) VALUES (?, ?, ?, ?)";

        String hashedPassword = UserDAO.hashPassword(userData.password());
        String json = new Gson().toJson(new UserData(userData.username(), hashedPassword, userData.email()));
        try {
            executeUpdate(statement, userData.username(), hashedPassword, userData.email(), json);
        } catch (DataAccessException e) {
            throw new DataAccessException("An error occurred. Your email may have already been taken.");
        }
    }

    public Map<String, UserData> listUsers() throws DataAccessException {
        HashMap<String, UserData> result = new HashMap<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM users";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String json = rs.getString("json");
                        UserData userData = new Gson().fromJson(json, UserData.class);
                        result.put(userData.username(), userData);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException();
        }
        return result;
    }
}
