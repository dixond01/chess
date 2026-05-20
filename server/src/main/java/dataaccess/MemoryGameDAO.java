package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private HashMap<Integer, GameData> games = new HashMap<>();
    public void deleteGame(GameData gameData) {games.remove(gameData.gameID());}

    public void deleteAllGames() {games.clear();}

    public Map<Integer, GameData> getGames() {
        return games;
    }

    public void setGames(Map<Integer, GameData> games) {
        this.games = (HashMap<Integer, GameData>) games;
    }
}
