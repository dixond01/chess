package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    @Test
    void testSuccessfulClear() throws DataAccessException {
        var userDAO = new MemoryUserDAO();
        var gameDAO = new MemoryGameDAO();
        var authDAO = new MemoryAuthDAO();

        userDAO.setUsers(new HashMap<>(Map.of("username", new UserData("username", "password", "email"))));
        gameDAO.setGames(new HashMap<>(Map.of(1, new GameData(1, "white", "black", "game", new ChessGame()))));
        authDAO.setAuths(new HashMap<>(Map.of("token", new AuthData("token", "username"))));

        var clearService = new ClearService(userDAO, gameDAO, authDAO);
        clearService.clear();

        assertTrue(userDAO.getUsers().isEmpty());
        assertTrue(gameDAO.getGames().isEmpty());
        assertTrue(authDAO.getAuths().isEmpty());
    }
}
