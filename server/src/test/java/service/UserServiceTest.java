package service;

import dataaccess.AlreadyTakenException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.RegisterRequest;
import service.result.RegisterResult;

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
}
