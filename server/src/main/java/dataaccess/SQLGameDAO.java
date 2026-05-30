package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.DataAccessException;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static dataaccess.DatabaseManager.executeUpdate;


public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`)
            )
            """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        var statement = "TRUNCATE TABLE games";
        executeUpdate(statement);
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameId, whiteUsername, blackUsername, gameName, json FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int gameID = rs.getInt("gameID");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        String json = rs.getString("json");
                        ChessGame game = new Gson().fromJson(json, ChessGame.class);
                        result.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException();
        }
        return result;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO games (gameName, json) VALUES (?, ?)";

        ChessGame chessGame = new ChessGame();
        String json = new Gson().toJson(chessGame);
        return executeUpdate(statement, gameName, json);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        gameID = rs.getInt("gameID");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        String json = rs.getString("json");
                        ChessGame chessGame = new Gson().fromJson(json, ChessGame.class);

                        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException();
        }
        return null;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        if (getGame(gameData.gameID()) == null) {
            throw new DataAccessException("game with that id does not exist");
        }
        var statement = "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, json=? WHERE gameID=?";
        String newWhiteUsername = gameData.whiteUsername();
        String newBlackUsername = gameData.blackUsername();
        String gameName = gameData.gameName();
        String newGame = new Gson().toJson(gameData.game());
        executeUpdate(statement, newWhiteUsername, newBlackUsername, gameName, newGame, gameData.gameID());
    }
}
