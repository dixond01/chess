package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static dataaccess.DatabaseManager.executeUpdate;
import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException{
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  auths (
              `username` varchar(256),
              `authToken` varchar(256) NOT NULL UNIQUE,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`authToken`)
            )
            """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        var statement = "DELETE FROM auths WHERE authToken=?";
        executeUpdate(statement, authData.authToken());
    }

    @Override
    public void deleteAllAuths() throws DataAccessException {
        var statement = "TRUNCATE TABLE auths";
        executeUpdate(statement);
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        var statement = "INSERT INTO auths (username, authToken, json) VALUES (?, ?, ?)";
        if (username == null) {
            throw new DataAccessException();
        }
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, username);
        String json = new Gson().toJson(authData);
        executeUpdate(statement, username, authToken, json);

        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, json FROM auths WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var json = rs.getString("json");
                        return new Gson().fromJson(json, AuthData.class);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException();
        }
        return null;
    }

    public Map<String, AuthData> listAuths() throws DataAccessException {
        HashMap<String, AuthData> result = new HashMap<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM auths";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String json = rs.getString("json");
                        AuthData authData = new Gson().fromJson(json, AuthData.class);
                        result.put(authData.authToken(), authData);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException();
        }
        return result;
    }
}
