package dataaccess;

import model.AuthData;

import java.util.Map;
import java.util.UUID;

public interface AuthDAO {
    void deleteAuth(AuthData authData) throws DataAccessException;
    void deleteAllAuths() throws DataAccessException;
    AuthData createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    Map<String, AuthData> listAuths() throws DataAccessException;
    default String generateToken() {
        return UUID.randomUUID().toString();
    }

}
