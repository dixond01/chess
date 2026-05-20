package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> auths = new HashMap<>();
    public void deleteAuth(AuthData authData){auths.remove(authData.username());}
    public void deleteAllAuths() {auths.clear();}
    public AuthData createAuth(String username) {
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, username);
        auths.put(username, authData);
        return authData;
    }
    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
