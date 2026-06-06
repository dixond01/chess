package dataaccess;

import model.exception.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Map;

public interface UserDAO {
    void deleteAllUsers() throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData userData) throws DataAccessException;
    static String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    Map<String, UserData> listUsers() throws DataAccessException;
}
