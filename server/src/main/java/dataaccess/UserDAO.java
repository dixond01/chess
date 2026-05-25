package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;

public interface UserDAO {
    void deleteAllUsers() throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData userData) throws DataAccessException;
    static String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }
}
