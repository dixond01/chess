package dataaccess;

import chess.ChessGame;
import model.DataAccessException;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private HashMap<Integer, GameData> games;
    private int idIncrement;

    public MemoryGameDAO() {
        this.games = new HashMap<>();
        this.idIncrement = 1;
    }

    public void deleteAllGames() {games.clear();}

    public List<GameData> listGames() {return new ArrayList<>(games.values());
    }

    public int createGame(String gameName) throws DataAccessException {
        if (gameName == null) {
            throw new DataAccessException();
        }
        int gameID = idIncrement;
        games.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
        idIncrement++;
        return gameID;
    }

    public GameData getGame(int gameID) {return games.get(gameID);}

    public void updateGame(GameData gameData) throws DataAccessException{
        if (!games.containsKey(gameData.gameID())) {
            throw new DataAccessException("game with that id does not exist");
        }
        games.put(gameData.gameID(), gameData);
    }

    public Map<Integer, GameData> getGames() {
        return games;
    }

    public void setGames(Map<Integer, GameData> games) {
        this.games = (HashMap<Integer, GameData>) games;
    }
}
