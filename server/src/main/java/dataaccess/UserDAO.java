package dataaccess;

import model.UserData;

public interface UserDAO {
    void deleteAllUsers();
    UserData getUser(String username);
    void createUser(UserData userData);
}
