package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    void deleteGame(GameData gameData);
    void deleteAllGames();
    List<GameData> listGames();
}
