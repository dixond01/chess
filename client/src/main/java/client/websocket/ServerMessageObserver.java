package client.websocket;

import model.exception.DataAccessException;
import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    void notify(ServerMessage serverMessage) throws DataAccessException;
}
