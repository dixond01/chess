package dataaccess;

public class UnauthorizedException extends Exception{
    public UnauthorizedException() {super("unauthorized");}
    public UnauthorizedException(String message) {
        super(message);
    }
    public UnauthorizedException(String message, Throwable ex) {
        super(message, ex);
    }
}
