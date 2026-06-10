package websocket.messages;

public class ErrorMessage extends ServerMessage {

    private final String error;

    //This message is sent to a client when it sends an invalid command.
    // The message must include the word Error.
    public ErrorMessage(ServerMessageType type, String error) {
        super(type);
        this.error = "Error: " + error;
    }

}
