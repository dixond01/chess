package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConnectionManager {
    //need to change this container to map with key gameId and value set of sessions in that game
    public final ConcurrentMap<Integer, HashSet<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> new HashSet<>()).add(session);
    }

    public void remove(int gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> new HashSet<>()).remove(session);
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        for (Session session : connections.get(gameID)) {
            if (session.isOpen() && !session.equals(excludeSession)) {
                    session.getRemote().sendString(msg);
                }

        }
    }

    public void messageRootClient(int gameID, Session rootSession, ServerMessage serverMessage) throws IOException {
        String message = serverMessage.toString();
        for (Session session : connections.get(gameID)) {
            if (session.isOpen() && session.equals(rootSession)) {
                session.getRemote().sendString(message);
            }
        }
    }
}
