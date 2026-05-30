package client;

import chess.ChessGame;
import model.DataAccessException;
import model.GameData;
import server.ServerFacade;
import service.request.CreateGameRequest;
import service.request.ListGamesRequest;
import service.request.LogoutRequest;
import service.result.CreateGameResult;
import service.result.ListGamesResult;

import java.util.ArrayList;

public class PostLoginClient implements Client{

    private final ServerFacade server;
    private ArrayList<GameData> gamesList;

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
    public String evaluateCommand(String cmd, String[] params) throws DataAccessException{
        return switch (cmd) {
            case ("logout") -> logout();
            case ("create") -> createGame(params);
            case ("list") -> listGames();
            case("play") -> playGame(params);
            case("observe") -> observeGame(params);
            case("help") -> help();
            default -> null;
        };
    }

    private String logout() throws DataAccessException {
        server.logout(new LogoutRequest(server.getAuthToken()));
        server.setAuthToken(null);
        new PreLoginClient(server).run();
        return null;
    }

    private String createGame(String[] params) throws DataAccessException {
        if (params.length < 1) {
            return "Please include a game name.";
        }
        CreateGameResult createGameResult = server.createGame(new CreateGameRequest(server.getAuthToken(), params[0]));
        gamesList.add(new GameData(createGameResult.gameID(), null, null, params[0], new ChessGame()));
        return String.format("Your gameID is: %d", gamesList.size());
    }

    private String listGames() throws DataAccessException {
        ListGamesResult listGamesResult = server.listGames(new ListGamesRequest(server.getAuthToken()));
        gamesList = (ArrayList<GameData>) listGamesResult.games();
        StringBuilder gamesString = new StringBuilder(100);
        if (gamesList.isEmpty()) {
            return "No games to display.";
        }
        for (int i = 0; i < gamesList.size(); i++) {
            gamesString.append(String.format("[%d]: %s ", i, gamesList.get(i).toString()));
        }
        return gamesString.toString();
    }

}
