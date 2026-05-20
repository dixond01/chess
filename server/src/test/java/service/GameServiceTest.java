package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.ListGamesRequest;
import service.result.ListGamesResult;

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
    void testSuccessfulListGames() throws UnauthorizedException {
        authDAO.setAuths(new HashMap<>(Map.of("token", new AuthData("token", "username"))));
        var gameData = new GameData(1, "white", "black", "game", new ChessGame());
        gameDAO.setGames(new HashMap<>(Map.of(1, gameData)));
        ListGamesResult games = gameService.listGames(new ListGamesRequest("token"));
        assertEquals(new ListGamesResult(List.of(gameData)), games);
    }

    @Test
    void testUnauthorizedListGames() {
        var gameData = new GameData(1, "white", "black", "game", new ChessGame());
        gameDAO.setGames(new HashMap<>(Map.of(1, gameData)));
        assertThrows(UnauthorizedException.class, () -> {
            gameService.listGames(new ListGamesRequest("token"));
        });
    }
}
