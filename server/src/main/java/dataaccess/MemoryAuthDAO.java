package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> auths = new HashMap<>();
    public void deleteAuth(AuthData authData){auths.remove(authData.authToken());}
    public void deleteAllAuths() {auths.clear();}
    public AuthData createAuth(String username) {
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, username);
        auths.put(authToken, authData);
        return authData;
    }
    public AuthData getAuth(String authToken) {return auths.get(authToken);}
    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public Map<String, AuthData> getAuths() {
        return auths;
    }

    public void setAuths(Map<String, AuthData> auths) {
        this.auths = (HashMap<String, AuthData>) auths;
    }
}
