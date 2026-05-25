package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void deleteAuth(AuthData authData) throws DataAccessException;
    //maybe deleteAllAuths
    void deleteAllAuths() throws DataAccessException;
    AuthData createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
}
