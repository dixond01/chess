package dataaccess;

import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private HashMap<String, GameData> games = new HashMap<>();
    public void deleteGame(GameData gameData) {games.remove(gameData.gameName());}

    public void deleteAllGames() {games.clear();}
}
