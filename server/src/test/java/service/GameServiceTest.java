package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.exception.AlreadyTakenException;
import model.exception.DataAccessException;
import model.GameData;
import model.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.request.ListGamesRequest;
import model.result.CreateGameResult;
import model.result.ListGamesResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameServiceTest {

    MemoryGameDAO gameDAO;
    MemoryAuthDAO authDAO;
    GameService gameService;

    @BeforeEach
    void setup() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);
    }

    @Test
    void testSuccessfulListGames() throws UnauthorizedException, DataAccessException {
        authDAO.setAuths(new HashMap<>(Map.of("token", new AuthData("token", "username"))));

        var gameData = new GameData(1, "white", "black", "game", new ChessGame());
        gameDAO.setGames(new HashMap<>(Map.of(1, gameData)));

        ListGamesResult games = gameService.listGames(new ListGamesRequest("token"));

        assertEquals(new ListGamesResult(List.of(gameData)), games);
    }

    @Test
    void testUnauthorizedListGames() throws DataAccessException {
        var gameData = new GameData(1, "white", "black", "game", new ChessGame());
        gameDAO.setGames(new HashMap<>(Map.of(1, gameData)));

        assertThrows(UnauthorizedException.class, () ->
            gameService.listGames(new ListGamesRequest("token"))
        );
    }

    @Test
    void testSuccessfulCreateGame() throws UnauthorizedException, DataAccessException{
        authDAO.setAuths(new HashMap<>(Map.of("token", new AuthData("token", "username"))));

        CreateGameResult result = gameService.createGame(new CreateGameRequest("token", "game"));

        assertEquals(new CreateGameResult(1), result);
    }

    @Test
    void testUnauthorizedCreateGame() throws DataAccessException{
        assertThrows(UnauthorizedException.class, () ->
            gameService.createGame(new CreateGameRequest("token", "game"))
        );
    }

    @Test
    void testSuccessfulJoinGame() throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        authDAO.setAuths(new HashMap<>(Map.of("token", new AuthData("token", "username"))));

        var gameData = new GameData(1, null, "black", "game", new ChessGame());
        gameDAO.setGames(new HashMap<>(Map.of(1, gameData)));
        gameService.joinGame(new JoinGameRequest("token", "WHITE", 1));
        GameData expectedGameData = new GameData(1, "username", gameData.blackUsername(), gameData.gameName(), gameData.game());
        assertEquals(new HashMap<>(Map.of(1, expectedGameData)), gameDAO.getGames());
    }

    @Test
    void testColorAlreadyTaken() throws DataAccessException {
        authDAO.setAuths(new HashMap<>(Map.of("token", new AuthData("token", "username"))));

        var gameData = new GameData(1, null, "black", "game", new ChessGame());
        gameDAO.setGames(new HashMap<>(Map.of(1, gameData)));

        assertThrows(AlreadyTakenException.class, () ->
            gameService.joinGame(new JoinGameRequest("token", "BLACK", 1))
        );
    }
}
