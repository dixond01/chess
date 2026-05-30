package client;

import server.Server;
import server.ServerFacade;
import service.request.LoginRequest;
import service.request.RegisterRequest;
import service.result.LoginResult;
import service.result.RegisterResult;

import java.util.Arrays;

public class PreLoginClient implements Client{
    private final ServerFacade server;

    public PreLoginClient(ServerFacade server) {
        this.server = server;
    }

    @Override
    public String startMessage() {
        return "Welcome to chess!";
    }

    @Override
    public String help() {
        return """
                - login <username> <password>
                - register <username> <password> (optional:)<email>
                - help
                - quit
                """;
    }

    @Override
    public String evaluateCommand(String cmd, String[] params) {
        return switch (cmd) {
            case ("login") -> login(params);
            case ("register") -> register(params);
            case ("help") -> help();
            default -> null;
        };
    }

    private String login(String[] params) {
        if (params.length < 2) {
            return "Please include both a username and password.";
        }
        try {
            LoginResult loginResult = server.login(new LoginRequest(params[0], params[1]));
            server.setAuthToken(loginResult.authToken());
            System.out.printf("Welcome to chess, %s!", loginResult.username());
            new PostLoginClient(server).run();
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
            server.setAuthToken(registerResult.authToken());
            System.out.printf("Welcome to chess, %s!", registerResult.username());
            new PostLoginClient(server).run();
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
