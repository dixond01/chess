package dataaccess;

import model.GameData;

public interface GameDAO {
    void deleteGame(GameData gameData) throws DataAccessException;

    void deleteAllGames();
}
