package dataaccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SQLUserDAOTest {

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
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void deleteAllUsers(Class<? extends UserDAO> dbClass) throws DataAccessException{


    }

    @Test
    void getUser() {
    }

    @Test
    void createUser() {
    }
}
