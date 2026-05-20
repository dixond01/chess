package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import service.request.CreateGameRequest;
import service.request.JoinGameRequest;
import service.request.ListGamesRequest;
import service.result.CreateGameResult;
import service.result.ListGamesResult;

public class GameService {
    private final GameDAO gameDAO;

    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

//    public ListGamesResult listGames(ListGamesRequest listGamesRequest) {}
//    public void joinGame(JoinGameRequest joinGameRequest) {}
//    public CreateGameResult createGame(CreateGameRequest createGameRequest) {}
}
