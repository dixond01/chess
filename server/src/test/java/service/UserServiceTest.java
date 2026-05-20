package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import service.result.LoginResult;
import service.result.RegisterResult;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
    void testSuccessfulLogin() throws UnauthorizedException {
        userDAO.setUsers(new HashMap<>(Map.of("username", new UserData("username", "password", "email"))));
        LoginResult result = userService.login(new LoginRequest("username", "password"));
        assertEquals("username", result.username());
    }

    @Test
    void testInvalidUsername() {
        userDAO.setUsers(new HashMap<>(Map.of("username", new UserData("username", "password", "email"))));
        assertThrows(UnauthorizedException.class, () -> {
            userService.login(new LoginRequest("typo", "password"));
        });
    }

    @Test
    void testSuccessfulLogout() throws UnauthorizedException {
        authDAO.setAuths(new HashMap<>(Map.of("token", new AuthData("token", "username"))));
        userService.logout(new LogoutRequest("token"));
        assertFalse(authDAO.getAuths().containsKey("token"));
    }

    @Test
    void testUnauthorizedLogout() {
        assertThrows(UnauthorizedException.class, () -> {
            userService.logout(new LogoutRequest("token"));
        });
    }
}
