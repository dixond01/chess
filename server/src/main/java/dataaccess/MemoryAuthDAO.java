package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> auths = new HashMap<>();
    public void deleteAuth(AuthData authData) throws DataAccessException {
        if (authData.authToken() == null) {
            throw new DataAccessException();
        }
        auths.remove(authData.authToken());}
    public void deleteAllAuths() throws DataAccessException {auths.clear();}
    public AuthData createAuth(String username) throws DataAccessException {
        if (username == null) {
            throw new DataAccessException();
        }
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, username);
        auths.put(authToken, authData);
        return authData;
    }
    public AuthData getAuth(String authToken) throws DataAccessException{
        if (authToken == null) {
            throw new DataAccessException();
        }
        return auths.get(authToken);}

    public Map<String, AuthData> listAuths() throws DataAccessException {
        return auths;
    }

    public void setAuths(Map<String, AuthData> auths) {
        this.auths = (HashMap<String, AuthData>) auths;
    }
}
