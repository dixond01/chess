package dataaccess;

public class InvalidLoginException extends Exception{
    public InvalidLoginException() {super("username or password incorrect");}
    public InvalidLoginException(String message) {
        super(message);
    }
    public InvalidLoginException(String message, Throwable ex) {
        super(message, ex);
    }
}
