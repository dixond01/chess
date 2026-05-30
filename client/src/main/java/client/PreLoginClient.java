package client;

import server.ServerFacade;
import service.request.LoginRequest;
import service.request.RegisterRequest;
import service.result.LoginResult;
import service.result.RegisterResult;

import java.util.Arrays;

public class PreLoginClient implements Client{
    private final String port;
    private final ServerFacade server;

    public PreLoginClient(String port) {
        this.port = port;
        this.server = new ServerFacade(port);
    }

    @Override
    public String evaluateCommand(String cmd, String[] params) {
        return switch (cmd) {
            case ("login") -> login(params);
            case ("register") -> register(params);
            default -> null;
        };
    }

    @Override
    public String help() {
        return """
                - login <username> <password>
                - register <username> <password> (optional:)<email>
                - quit
                """;
    }

    @Override
    public String startMessage() {
        return "Welcome to chess!";
    }

    //login
    private String login(String[] params) {
        if (params.length < 2) {
            return "Please include both a username and password.";
        }
        try {
            LoginResult loginResult = server.login(new LoginRequest(params[0], params[1]));
            System.out.printf("Welcome to chess, %s!", loginResult.username());
            new PostLoginClient(port).run();
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String register(String[] params) {
        if (params.length < 2) {
            return "Please include a username, password, and an optional email to register.";
        }
        if (params.length < 3) {
            params = Arrays.copyOf(params, 3);
            params[2] = null;
        }
        try {
            RegisterResult registerResult = server.register(new RegisterRequest(params[0], params[1], params[2]));
            System.out.printf("Welcome to chess, %s!", registerResult.username());
            new PostLoginClient(port).run();
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
