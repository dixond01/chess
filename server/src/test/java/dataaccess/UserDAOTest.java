package dataaccess;

import model.DataAccessException;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private UserDAO getDataAccess(Class<? extends UserDAO> databaseClass) throws DataAccessException {
        UserDAO userDAO;
        if (databaseClass.equals(SQLUserDAO.class)) {
            userDAO = new SQLUserDAO();
        } else {
            userDAO = new MemoryUserDAO();
        }
        userDAO.deleteAllUsers();
        return userDAO;
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void deleteAllUsers(Class<? extends UserDAO> userDAOClass) throws DataAccessException{
        UserDAO userDAO = getDataAccess(userDAOClass);

        userDAO.createUser(new UserData("username", "password", "email"));

        userDAO.deleteAllUsers();

        Collection<UserData> actual = userDAO.listUsers().values();
        assertEquals(0, actual.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void getUserSuccess(Class<? extends UserDAO> userDAOClass) throws DataAccessException{
        UserDAO userDAO = getDataAccess(userDAOClass);

        userDAO.createUser(new UserData("username", "password", "email"));

        UserData userData = userDAO.getUser("username");

        assertEquals("username", userData.username());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void getUserFailure(Class<? extends UserDAO> userDAOClass) throws DataAccessException{
        UserDAO userDAO = getDataAccess(userDAOClass);

        assertNull(userDAO.getUser("username"));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void createUserSuccess(Class<? extends UserDAO> userDAOClass) throws DataAccessException{
        UserDAO userDAO = getDataAccess(userDAOClass);

        userDAO.createUser(new UserData("username", "password", "email"));

        Collection<UserData> actual = userDAO.listUsers().values();

        assertEquals(1, actual.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void createUserFailure(Class<? extends UserDAO> userDAOClass) throws DataAccessException{
        UserDAO userDAO = getDataAccess(userDAOClass);

        assertThrows(DataAccessException.class, () ->
                userDAO.createUser(new UserData(null, "password", "email")));
    }

}
