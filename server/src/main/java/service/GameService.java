package service;

import dataaccess.*;
import model.AuthData;
import model.DataAccessException;
import model.GameData;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.request.ListGamesRequest;
import model.result.CreateGameResult;
import model.result.ListGamesResult;

import java.util.List;
import java.util.Objects;

public class GameService {
    private final GameDAO gameDAO;

    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws UnauthorizedException, DataAccessException {
        AuthData authData = authDAO.getAuth(listGamesRequest.authToken());
        if (authData == null) {
            throw new UnauthorizedException();
        }
        List<GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }
    public void joinGame(JoinGameRequest joinGameRequest)
            throws UnauthorizedException, AlreadyTakenException, BadRequestException, DataAccessException {
        AuthData authData = authDAO.getAuth(joinGameRequest.authToken());
        if (authData == null) {
            throw new UnauthorizedException();
        }

        GameData gameData = gameDAO.getGame(joinGameRequest.gameID());
        if (gameData == null) {
            throw new BadRequestException("must enter valid gameID.");
        }
        if ((Objects.equals(joinGameRequest.playerColor(), "WHITE") && (gameData.whiteUsername() != null) && (!Objects.equals(gameData.whiteUsername(), authData.username()))
        || (Objects.equals(joinGameRequest.playerColor(), "BLACK") && (gameData.blackUsername() != null) && (!Objects.equals(gameData.blackUsername(), authData.username())))))  {
            throw new AlreadyTakenException("color already taken.");
        }

        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        if (Objects.equals(joinGameRequest.playerColor(), "WHITE")) {
            whiteUsername = authData.username();
        } else {
            blackUsername = authData.username();
        }

        gameDAO.updateGame(new GameData(joinGameRequest.gameID(), whiteUsername, blackUsername, gameData.gameName(), gameData.game()));
    }
    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws UnauthorizedException, DataAccessException {
        AuthData authData = authDAO.getAuth(createGameRequest.authToken());
        if (authData == null) {
            throw new UnauthorizedException();
        }
        return new CreateGameResult(gameDAO.createGame(createGameRequest.gameName()));
    }
}
