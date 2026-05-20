package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private HashMap<String, UserData> users = new HashMap<>();

    public void deleteUser(UserData userData){users.remove(userData.username());}

    public void deleteAllUsers() {users.clear();}

    public UserData getUser(String username) {return users.get(username);}

    public void createUser(UserData userData) {
        users.put(userData.username(), userData);
    }

    public HashMap<String, UserData> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, UserData> users) {
        this.users = users;
    }
}
