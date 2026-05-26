package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void deleteAllUsers(Class<? extends UserDAO> userDAOClass) throws DataAccessException{
        UserDAO userDAO = getDataAccess(userDAOClass);

        userDAO.addUser(new Pet(0, "joe", PetType.FISH));
        userDAO.addPet(new Pet(0, "sally", PetType.CAT));

        userDAO.deleteAllUsers();

        Collection<UserData> actual = userDAO.listUsers().values();
        assertEquals(0, actual.size());

    }

    @Test
    void getUser() {
    }

    @Test
    void createUser() {
    }
}
