package client;

import model.exception.DataAccessException;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.LoginResult;
import model.result.RegisterResult;
import static ui.EscapeSequences.*;

import java.util.Arrays;

public class PreLoginClient implements Client{
    private final ServerFacade server;

    public PreLoginClient(ServerFacade server) {
        this.server = server;
    }

    @Override
    public String startMessage() {
        return "Welcome to chess! Please login.";
    }

    @Override
    public String help() {
        return """
                - login <username> <password>
                - register <username> <password> <email>
                - help
                - quit
                """;
    }

    @Override
    public String evaluateCommand(String cmd, String[] params) throws DataAccessException {
        return switch (cmd) {
            case ("login") -> login(params);
            case ("register") -> register(params);
            case ("help") -> help();
            default -> null;
        };
    }

    @Override
    public void quit() {
        System.out.println("Thank you for playing!");
        System.exit(0);
    }

    private String login(String[] params) throws DataAccessException{
        if (params.length < 2) {
            return "Please include both a username and password.";
        }
        LoginResult loginResult = server.login(new LoginRequest(params[0], params[1]));
        System.out.printf("%sWelcome to chess, %s!%s", SET_TEXT_COLOR_LIGHT_GREY, loginResult.username(), SET_TEXT_COLOR_WHITE);
        new PostLoginClient(server).run();
        return null;
    }

    private String register(String[] params) throws DataAccessException {
        if (params.length < 2) {
            return "Please include a username, password, and an email to register.";
        }
        if (params.length < 3) {
            params = Arrays.copyOf(params, 3);
            params[2] = null;
        }
        RegisterResult registerResult = server.register(new RegisterRequest(params[0], params[1], params[2]));
        System.out.printf("%sWelcome to chess, %s!%s", SET_TEXT_COLOR_LIGHT_GREY, registerResult.username(), SET_TEXT_COLOR_WHITE);
        new PostLoginClient(server).run();
        return null;
    }
}
