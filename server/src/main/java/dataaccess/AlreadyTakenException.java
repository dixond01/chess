package dataaccess;

public class AlreadyTakenException extends Exception {
    public AlreadyTakenException() {super("username already taken");}
    public AlreadyTakenException(String message) {
        super(message);
    }
    public AlreadyTakenException(String message, Throwable ex) {
        super(message, ex);
    }
}
