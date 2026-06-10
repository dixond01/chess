package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import io.javalin.websocket.*;
import model.GameData;
import model.exception.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    private SQLAuthDAO authDAO;
    private SQLGameDAO gameDAO;

    public WebSocketHandler() {
        try {
            this.authDAO = new SQLAuthDAO();
            this.gameDAO = new SQLGameDAO();
        } catch (DataAccessException e) {
            System.out.println("Error: unable to connect to server");
        }
    }


    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        int gameId = -1;
        Session session = wsMessageContext.session;

        try {
            UserGameCommand command = new Gson().fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());
            saveSession(gameId, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> {
                    command = new Gson().fromJson(wsMessageContext.message(), MakeMoveCommand.class);
                    makeMove(session, username, (MakeMoveCommand) command);
                }
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(username, command);
            }
        } catch (IOException e) {
            sendMessage(session, gameId, "unable to connect to players");
        } catch (Exception e){
            sendMessage(session, gameId, e.getMessage());
        }
    }


    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Session session, String username, UserGameCommand command) throws DataAccessException {
        try {
            GameData game = gameDAO.getGame(command.getGameID());
            var loadGameMessage = new LoadGameMessage(LOAD_GAME, game.game());
            connections.messageRootClient(session, loadGameMessage);
            String message;
            if (Objects.equals(username, game.whiteUsername())) {
                message = String.format("%s joined the game as the white player!", username);
            } else if (Objects.equals(username, game.blackUsername())) {
                message = String.format("%s joined the game as the black player!", username);
            } else {
                message = String.format("%s is now observing the game!", username);
            }
            var notification = new NotificationMessage(NOTIFICATION, message);
            connections.broadcast(game.gameID(), session, notification);
        } catch (DataAccessException | IOException e) {
            throw new DataAccessException("a game with that ID does not exist");
        }
    }

    private void makeMove(Session session, String username, MakeMoveCommand command)
            throws DataAccessException, IOException, InvalidMoveException {
        GameData gameData;
        try {
            gameData = gameDAO.getGame(command.getGameID());
        } catch (DataAccessException e) {
            throw new DataAccessException("a game with that ID does not exist");
        }

        //check color matches and it's the player's turn
        ChessMove move = command.getMove();
        ChessGame.TeamColor userColor = getUserColor(command.getAuthToken(), gameData);
        ChessPiece pieceToMove = gameData.game().getBoard().getPiece(move.getStartPosition());
        if (userColor != pieceToMove.getTeamColor()) {
            throw new InvalidMoveException("you cannot move your opponent's pieces.");
        }
        if (userColor != gameData.game().getTeamTurn()) {
            throw new InvalidMoveException("it is not your turn. Please wait until your opponent makes a move.");
        }

        //make move
        gameData.game().makeMove(move);
        gameDAO.updateGame(gameData);

        //update game boards
        var updateNotification = new LoadGameMessage(LOAD_GAME, gameData.game());
        connections.broadcast(gameData.gameID(), null, updateNotification);

        //send move notification to all but root
        String startString = move.getStartPosition().toString();
        String endString = move.getEndPosition().toString();
        String moveMessage = String.format("%s moved from %s to %s", username, startString, endString);
        var moveNotification = new NotificationMessage(NOTIFICATION, moveMessage);
        connections.broadcast(gameData.gameID(), session, moveNotification);

        //check for win conditions and set game as complete
        ChessGame game = gameData.game();
        String msg = null;
        if (game.isInCheckmate(WHITE)) {
            msg = "The white team is in checkmate. The black team wins!";
            game.setGameOver(true);
            //handle ending game
        } else if (game.isInCheckmate(BLACK)) {
            msg = "The black team is in checkmate. The white team wins!";
            game.setGameOver(true);
            //handle ending game
        } else if (game.isInStalemate(WHITE)) {
            msg = "The white team is in stalemate. The game ends in a draw!";
            game.setGameOver(true);
            //handle ending game
        } else if (game.isInStalemate(BLACK)) {
            msg = "The black team is in stalemate. The game ends in a draw!";
            game.setGameOver(true);
            //handle ending game
        } else if (game.isInCheck(WHITE)) {
            msg = "The white team is in check!";
        } else if (game.isInCheck(BLACK)) {
            msg = "The black team is in check!";
        }
        if (msg != null) {
            var gameStatusNotification = new NotificationMessage(NOTIFICATION, msg);
            connections.broadcast(gameData.gameID(), null, gameStatusNotification);
        }
        if (game.isGameOver()) {
            gameDAO.updateGame(gameData);
        }
    }

    private void leaveGame(Session session, String username, UserGameCommand command)
            throws IOException, DataAccessException {
        GameData gameData = gameDAO.getGame(command.getGameID());
        ChessGame.TeamColor userColor = getUserColor(command.getAuthToken(), gameData);
        if (userColor != null) {
            GameData gameWithoutPlayer = new GameData(gameData.gameID(), null,
                    gameData.blackUsername(), gameData.gameName(), gameData.game());
            if (Objects.equals(username, gameData.blackUsername())) {
                gameWithoutPlayer = new GameData(gameData.gameID(), gameData.whiteUsername(),
                        null, gameData.gameName(), gameData.game());
            }
            gameDAO.updateGame(gameWithoutPlayer);
        }

        var message = String.format("%s left the game", username);
        var notification = new NotificationMessage(NOTIFICATION, message);
        connections.broadcast(gameData.gameID(), session, notification);
        connections.remove(command.getGameID(), session);
    }

    public void resign(String username, UserGameCommand command)
            throws DataAccessException, IOException {
        GameData gameData = gameDAO.getGame(command.getGameID());
        if (gameData.game().isGameOver()) {
            throw new DataAccessException("the game is over. You cannot resign.");
        }
        gameData.game().setGameOver(true);
        gameDAO.updateGame(gameData);

        String resigningTeam;
        String winningTeam;
        if (Objects.equals(username, gameData.blackUsername())) {
            resigningTeam = "black";
            winningTeam = "white";
        } else if (Objects.equals(username, gameData.whiteUsername())) {
            resigningTeam = "white";
            winningTeam = "black";
        } else {
            throw new DataAccessException("must be a player to resign");
        }
        var message = String.format("The %s team resigned. The %s team wins!", resigningTeam, winningTeam);
        var notification = new NotificationMessage(NOTIFICATION, message);
        connections.broadcast(gameData.gameID(), null, notification);
    }

    private String getUsername(String authToken) throws DataAccessException {
        String username;
        try {
            username = authDAO.getAuth(authToken).username();
        } catch (Exception e) {
            throw new DataAccessException("cannot get username from database");
        }
        return username;
    }

    private void saveSession(int gameID, Session session) {
        connections.add(gameID, session);
    }

    private void sendMessage(Session session, int gameID, String message) throws IOException {
        ErrorMessage errorMessage = new ErrorMessage(ERROR, message);
        connections.messageRootClient(session, errorMessage);
    }

    private ChessGame.TeamColor getUserColor(String authToken, GameData gameData) throws DataAccessException {
        String username = getUsername(authToken);
        if (Objects.equals(username, gameData.whiteUsername())) {
            return WHITE;
        } else if (Objects.equals(username, gameData.blackUsername())) {
            return BLACK;
        }
        return null;
    }
}