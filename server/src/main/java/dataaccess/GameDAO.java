package dataaccess;

import model.exception.DataAccessException;
import model.GameData;

import java.util.List;

public interface GameDAO {
    void deleteAllGames() throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
}
