package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private HashMap<String, UserData> users = new HashMap<>();
    public void deleteUser(UserData userData){users.remove(userData.username());}
    public void deleteAllUsers() {users.clear();}

}
