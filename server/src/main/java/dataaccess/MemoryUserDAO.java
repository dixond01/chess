package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private HashMap<String, UserData> users = new HashMap<>();

    public void deleteAllUsers() {users.clear();}

    public UserData getUser(String username) {return users.get(username);}

    public void createUser(UserData userData) {
        userData = new UserData(userData.username(), UserDAO.hashPassword(userData.password()), userData.email());
        users.put(userData.username(), userData);
    }

    public Map<String, UserData> listUsers() {
        return users;
    }

    public void setUsers(Map<String, UserData> users) {
        this.users = (HashMap<String, UserData>) users;
    }
}
