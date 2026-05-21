package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.GameData;
import service.request.CreateGameRequest;
import service.request.JoinGameRequest;
import service.request.ListGamesRequest;
import service.result.CreateGameResult;
import service.result.ListGamesResult;

import java.util.List;

public class GameService {
    private final GameDAO gameDAO;

    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws UnauthorizedException {
        AuthData authData = authDAO.getAuth(listGamesRequest.authToken());
        if (authData == null) {
            throw new UnauthorizedException();
        }
        List<GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }
//    public void joinGame(JoinGameRequest joinGameRequest) {}
    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws UnauthorizedException {
        AuthData authData = authDAO.getAuth(createGameRequest.authToken());
        if (authData == null) {
            throw new UnauthorizedException();
        }
        return new CreateGameResult(gameDAO.createGame(createGameRequest.gameName()));
    }
}
