package server.websocket;

import com.google.gson.Gson;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import io.javalin.websocket.*;
import model.exception.DataAccessException;
import model.exception.UnauthorizedException;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    private final SQLAuthDAO authDAO = new SQLAuthDAO();
    private final SQLGameDAO gameDAO = new SQLGameDAO();

    public WebSocketHandler() throws DataAccessException {
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
                case CONNECT -> connect(session, username, (UserGameCommand) command);
                case MAKE_MOVE -> {
                    command = new Gson().fromJson(wsMessageContext.message(), MakeMoveCommand.class);
                    makeMove(session, username, (MakeMoveCommand) command);
                }
                case LEAVE -> leaveGame(session, username, (UserGameCommand) command);
                case RESIGN -> resign(session, username, (UserGameCommand) command);
            }
        } catch (UnauthorizedException ex) {
            sendMessage(session, gameId, "Error: unauthorized");
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session, gameId, "Error: " + ex.getMessage());
        }
    }


    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    //methods to handle messages from client?
    //connect
    //make move
    //leaveGame
    //resign
    private void enter(String visitorName, Session session) throws IOException {
        connections.add(session);
        var message = String.format("%s is in the shop", visitorName);
        var notification = new Notification(Notification.Type.ARRIVAL, message);
        connections.broadcast(session, notification);
    }

    private void exit(String visitorName, Session session) throws IOException {
        var message = String.format("%s left the shop", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(session, notification);
        connections.remove(session);
    }

    public void makeNoise(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new Notification(Notification.Type.NOISE, message);
            connections.broadcast(null, notification);
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private String getUsername(String authToken) throws DataAccessException {
        String username;
        try {
            username = authDAO.getAuth(authToken).username();
        } catch (DataAccessException e) {
            throw new DataAccessException("cannot get username from database");
        }
        return username;
    }

    private void saveSession(int gameID, Session session) {
        connections.add(gameID, session);
    }
}