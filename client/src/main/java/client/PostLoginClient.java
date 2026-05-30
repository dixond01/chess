package client;

import model.DataAccessException;
import server.ServerFacade;
import service.request.LogoutRequest;

public class PostLoginClient implements Client{

    private final ServerFacade server;

    public PostLoginClient(ServerFacade server) {
        this.server = server;
    }

    @Override
    public String startMessage() {
        return "Please input an action:";
    }

    @Override
    public String help() {
        return """
                - logout
                - create <gameName>
                - list
                - play <gameID>
                - observe <gameID>
                - help
                - quit
                """;
    }

    @Override
    public String evaluateCommand(String cmd, String[] params) {
        return switch (cmd) {
            case ("logout") -> logout();
            case ("create") -> createGame(params);
            case ("list") -> listGames(params);
            case("play") -> playGame(params);
            case("observe") -> observeGame(params);
            case("help") -> help();
            default -> null;
        };
    }
}
