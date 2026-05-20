package dataaccess;

import model.UserData;

public interface UserDAO {
    void deleteUser(UserData userData) throws DataAccessException;
    //maybe deleteAllUsers for clear?
    void deleteAllUsers();
}
