package client;

import chess.ChessGame;
import model.DataAccessException;
import model.GameData;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.request.ListGamesRequest;
import model.request.LogoutRequest;
import model.result.CreateGameResult;
import model.result.ListGamesResult;

import java.util.ArrayList;

public class PostLoginClient implements Client{

    private final ServerFacade server;
    private ArrayList<GameData> gamesList;

    public PostLoginClient(ServerFacade server) {
        this.server = server;
    }

    @Override
    public String startMessage() {
        return "\nPlease input an action:";
    }

    @Override
    public String help() {
        return """
                - logout
                - create <gameName>
                - list
                - play <team color (WHITE or BLACK)> <gameID>
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
            gamesString.append(String.format("[%d]: %s%n", i + 1, gamesList.get(i).toString()));
        }
        return gamesString.toString();
    }

    private String playGame(String[] params) throws DataAccessException {
        if (gamesList == null || gamesList.isEmpty()) {
            return "Please list games first.";
        }
        if (params.length < 2) {
            return "Please include team color and gameID.";
        }
        if ("white".equalsIgnoreCase(params[0])) {
            params[0] = "WHITE";
        } else if ("black".equalsIgnoreCase(params[0])) {
            params[0] = "BLACK";
        } else {
            return "Please adjust format: 'play <color> <gameID>'";
        }
        int listID = Integer.parseInt(params[1]);
        GameData game;
        try {
            game = gamesList.get(listID - 1);
        } catch (IndexOutOfBoundsException e) {
            return "Please include a valid gameID.";
        }
        server.joinGame(new JoinGameRequest(server.getAuthToken(), params[0], game.gameID()));
        //Open a WebSocket connection with the server (using the /ws endpoint) in order to send and receive gameplay messages.
        //Send a CONNECT WebSocket message to the server.
        //right now, I'm creating the websocketfacade object in the gameplayclient, so I don't know how I'll opent the websocket connection
        new GameplayClient(server, game, ParticipantType.PLAYER).run();
        return null;
    }

    private String observeGame(String[] params) {
        if (gamesList == null || gamesList.isEmpty()) {
            return "Please list games first.";
        }
        if (params.length < 1) {
            return "Please include gameID.";
        }
        int listID = Integer.parseInt(params[0]);
        try {
            GameData game = gamesList.get(listID - 1);
            new GameplayClient(server, game, ParticipantType.OBSERVER).run();
        } catch (IndexOutOfBoundsException | DataAccessException e) {
            return "Please include a valid gameID.";
        }
        return null;
    }

    @Override
    public void quit() throws DataAccessException {
        server.logout(new LogoutRequest(server.getAuthToken()));
        System.out.printf("See you later, %s!%n", server.getUsername());
        server.setUsername(null);
        System.exit(0);
    }

}
