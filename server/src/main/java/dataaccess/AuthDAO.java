package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void deleteAuth(AuthData authData) throws DataAccessException;
    //maybe deleteAllAuths
    void deleteAllAuths();
    AuthData createAuth(String username);
}
