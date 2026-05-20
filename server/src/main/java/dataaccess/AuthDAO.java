package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void deleteAuth(AuthData authData);
    //maybe deleteAllAuths
    void deleteAllAuths();
    AuthData createAuth(String username);
    AuthData getAuth(String authToken);
}
