package websocket.messages;

public class NotificationMessage extends ServerMessage {

    private final String notification;

    //This is a message meant to inform a player when another player made an action.
    public NotificationMessage(ServerMessageType type, String notification) {
        super(type);
        this.notification = notification;
    }

    public String getNotification() {
        return notification;
    }

    @Override
    public String toString() {
        return notification;
    }
}
