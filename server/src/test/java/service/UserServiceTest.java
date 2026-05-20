package service;

import dataaccess.AlreadyTakenException;
import dataaccess.InvalidLoginException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.LoginRequest;
import service.request.RegisterRequest;
import service.result.LoginResult;
import service.result.RegisterResult;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {
    MemoryUserDAO userDAO;
    MemoryAuthDAO authDAO;
    UserService userService;

    @BeforeEach
    void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }
    @Test
    void testSuccessfulRegister() throws AlreadyTakenException {
        RegisterResult result = userService.register(new RegisterRequest("username", "password", "email"));

        assertEquals("username", result.username());
    }

    @Test
    void testUsernameAlreadyTaken() throws AlreadyTakenException {
        userService.register(new RegisterRequest("username", "password", "email"));
        assertThrows(AlreadyTakenException.class, () -> {
            userService.register(new RegisterRequest("username", "anotherPassword", "anotherEmail"));

        });
    }

    @Test
    void testSuccessfulLogin() throws InvalidLoginException {
        userDAO.setUsers(new HashMap<>(Map.of("username", new UserData("username", "password", "email"))));
        LoginResult result = userService.login(new LoginRequest("username", "password"));
        assertEquals("username", result.username());
    }

    @Test
    void testInvalidUsername() {
        userDAO.setUsers(new HashMap<>(Map.of("username", new UserData("username", "password", "email"))));
        assertThrows(InvalidLoginException.class, () -> {
            userService.login(new LoginRequest("typo", "password"));
        });
    }
}
