package client;

import chess.ChessGame;
import model.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import service.request.*;
import service.result.ListGamesResult;
import service.result.LoginResult;
import service.result.RegisterResult;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;


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

    private RegisterResult registerDefault() throws DataAccessException {
       return serverFacade.register(new RegisterRequest("username", "password", "email"));
    }

    @Test
    void registerSuccess() throws DataAccessException {
        RegisterResult actual = registerDefault();
        RegisterResult expected = new RegisterResult("username", "token");
        assertEquals(expected.username(), actual.username());
    }

    @Test
    void registerFailure() {
        assertThrows(DataAccessException.class, this::registerDefault);
    }

    @Test
    void loginSuccess() throws DataAccessException {
        registerDefault();
        LoginResult loginResult = serverFacade.login(new LoginRequest("username", "password"));
        assertEquals("username", loginResult.username());
    }

    @Test
    void loginFailure() {
        assertThrows(DataAccessException.class, () -> {
            serverFacade.login(new LoginRequest("username", "password"));
        });
    }

    @Test
    void logoutSuccess() throws DataAccessException{
        String authToken = registerDefault().authToken();
        assertNotNull(serverFacade.getAuthToken());
        serverFacade.logout(new LogoutRequest(authToken));
        assertNull(serverFacade.getAuthToken());
    }

    @Test
    void logoutFailure() {
        assertThrows(DataAccessException.class, () -> {
            serverFacade.logout(new LogoutRequest("authToken"));
        });
    }

    void createGame(String authToken) throws DataAccessException {
        serverFacade.createGame(new CreateGameRequest(authToken, "game1"));
    }

    @Test
    void listGamesSuccess() throws DataAccessException {
        String authToken = registerDefault().authToken();
        createGame(authToken);
        ListGamesResult actual = serverFacade.listGames(new ListGamesRequest(authToken));
        GameData expected = new GameData(1, null, null, "game1", new ChessGame());
        assertEquals(expected, actual.games().getFirst());

    }

    @Test
    void listGamesFailure() {
        assertThrows(DataAccessException.class, () -> {
            serverFacade.listGames(new ListGamesRequest("token"));
        });
    }

}
