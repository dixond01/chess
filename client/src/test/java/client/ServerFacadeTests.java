package client;

import model.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import service.request.LoginRequest;
import service.request.RegisterRequest;
import service.result.LoginResult;
import service.result.RegisterResult;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(String.valueOf(port));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clear() throws DataAccessException {
        serverFacade.clear();
    }

    @Test
    public void registerSuccess() throws DataAccessException {
        RegisterResult actual = serverFacade.register(new RegisterRequest("username", "password", "email"));
        RegisterResult expected = new RegisterResult("username", "token");
        assertEquals(expected.username(), actual.username());
    }

    @Test
    void registerFailure() {
        assertThrows(DataAccessException.class, () -> {
            serverFacade.register(new RegisterRequest("username", "password", null));
        });
    }

    @Test
    void loginSuccess() throws DataAccessException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("username", "password", "email"));
        LoginResult loginResult = serverFacade.login(new LoginRequest("username", "password"));
        assertEquals("username", loginResult.username());
    }

    @Test
    void loginFailure() {
        assertThrows(DataAccessException.class, () -> {
            serverFacade.login(new LoginRequest("username", "password"));
        });
    }

}
