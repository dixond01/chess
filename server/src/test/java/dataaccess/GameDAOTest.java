package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {

    private GameDAO getDataAccess(Class<? extends GameDAO> databaseClass) throws DataAccessException {
        GameDAO gameDAO;
        if (databaseClass.equals(SQLGameDAO.class)) {
            gameDAO = new SQLGameDAO();
        } else {
            gameDAO = new MemoryGameDAO();
        }
        gameDAO.deleteAllGames();
        return gameDAO;
    }
    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void deleteAllGamesSuccess(Class<? extends GameDAO> gameDAOClass) throws DataAccessException{
        GameDAO gameDAO = getDataAccess(gameDAOClass);

        gameDAO.createGame("game1");
        gameDAO.createGame("game2");
        gameDAO.createGame("game3");

        gameDAO.deleteAllGames();

        Collection<GameData> actual = gameDAO.listGames();
        assertEquals(0, actual.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void listGamesSuccess(Class<? extends GameDAO> gameDAOClass) throws DataAccessException{
        GameDAO gameDAO = getDataAccess(gameDAOClass);

        gameDAO.createGame("game1");
        gameDAO.createGame("game2");
        gameDAO.createGame("game3");

        Collection<GameData> actual = gameDAO.listGames();
        assertEquals(3, actual.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void createGameSuccess(Class<? extends GameDAO> gameDAOClass) throws DataAccessException{
        GameDAO gameDAO = getDataAccess(gameDAOClass);

        int id = gameDAO.createGame("game1");

        assertEquals(1, id);
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void createGameFailure(Class<? extends GameDAO> gameDAOClass) throws DataAccessException{
        GameDAO gameDAO = getDataAccess(gameDAOClass);
        assertThrows(DataAccessException.class, () ->
                gameDAO.createGame(null));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void getGameSuccess(Class<? extends GameDAO> gameDAOClass) throws DataAccessException{
        GameDAO gameDAO = getDataAccess(gameDAOClass);

        int id = gameDAO.createGame("game1");
        GameData gameData = gameDAO.getGame(id);

        assertEquals("game1", gameData.gameName());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void getGameFailure(Class<? extends GameDAO> gameDAOClass) throws DataAccessException{
        GameDAO gameDAO = getDataAccess(gameDAOClass);

        gameDAO.createGame("game1");
        GameData gameData = null;
        try {
            gameData = gameDAO.getGame(10);
        } catch (DataAccessException e) {
            //do nothing
        }

        assertNull(gameData);
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void updateGameSuccess(Class<? extends GameDAO> gameDAOClass) throws DataAccessException{
        GameDAO gameDAO = getDataAccess(gameDAOClass);

        int id = gameDAO.createGame("game1");
        GameData gameData = new GameData(id, "white", null, "game1", new ChessGame());

        assertEquals("white", gameData.whiteUsername());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void updateGameFailure(Class<? extends GameDAO> gameDAOClass) throws DataAccessException{
        GameDAO gameDAO = getDataAccess(gameDAOClass);

        gameDAO.createGame("game1");
        GameData gameData = new GameData(10, "white", "black", "gameName", new ChessGame());
        assertThrows(DataAccessException.class, () ->
                gameDAO.updateGame(gameData)
        );
    }
}
