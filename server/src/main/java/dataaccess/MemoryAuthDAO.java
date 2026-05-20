package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> auths = new HashMap<>();
    public void deleteAuth(AuthData authData){auths.remove(authData.username());}
    public void deleteAllAuths() {auths.clear();}
}
