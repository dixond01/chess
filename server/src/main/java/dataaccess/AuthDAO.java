package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void deleteAuth(AuthData authData) throws DataAccessException;
    //maybe deleteAllAuths
}
