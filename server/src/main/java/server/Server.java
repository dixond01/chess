package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.DataAccessException;
import service.BadRequestException;
import service.ClearService;
import service.GameService;
import service.UserService;
import model.request.*;
import model.result.CreateGameResult;
import model.result.LoginResult;
import model.result.RegisterResult;
import model.result.ListGamesResult;

import java.util.Map;
import java.util.Objects;

public class Server {

    private final Javalin javalin;
    private static final UserDAO USER_DAO;

    static {
        try {
            USER_DAO = new SQLUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static final GameDAO GAME_DAO;

    static {
        try {
            GAME_DAO = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static final AuthDAO AUTH_DAO;

    static {
        try {
            AUTH_DAO = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;

    public Server() {
        this(new ClearService(USER_DAO, GAME_DAO, AUTH_DAO), new GameService(GAME_DAO, AUTH_DAO), new UserService(USER_DAO, AUTH_DAO));
    }

    public Server(ClearService clearService, GameService gameService, UserService userService) {
        this.clearService = clearService;
        this.gameService = gameService;
        this.userService = userService;

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .exception(BadRequestException.class, (e, ctx) -> {
                    ctx.status(400);
                    errorResult(e, ctx);
                })
                .exception(UnauthorizedException.class, (e, ctx) -> {
                    ctx.status(401);
                    errorResult(e,ctx);
                })
                .exception(AlreadyTakenException.class, (e, ctx) -> {
                    ctx.status(403);
                    errorResult(e, ctx);
                })
                .exception(DataAccessException.class, (e, ctx) -> {
                    ctx.status(500);
                    errorResult(e, ctx);
                });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void errorResult(Exception e, Context ctx) {
        ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
    }

    private void clear(Context ctx) throws DataAccessException{
        clearService.clear();
        ctx.status(200);
    }
    private void register(Context ctx) throws DataAccessException, AlreadyTakenException, BadRequestException {
        RegisterRequest registerRequest = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        if (isFieldBlank(registerRequest.username()) || isFieldBlank(registerRequest.password()) || isFieldBlank(registerRequest.email())) {
            throw new BadRequestException("must include username, password, and email");
        }
        RegisterResult registerResult = userService.register(registerRequest);
        ctx.result(new Gson().toJson(registerResult));
        ctx.status(200);
    }

    private void login(Context ctx) throws DataAccessException, BadRequestException, UnauthorizedException {
        LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);
        if (isFieldBlank(loginRequest.username()) || isFieldBlank(loginRequest.password())) {
            throw new BadRequestException("must include username and password");
        }
        LoginResult loginResult = userService.login(loginRequest);
        ctx.result(new Gson().toJson(loginResult));
        ctx.status(200);
    }

    private void logout(Context ctx) throws DataAccessException, UnauthorizedException {
        LogoutRequest logoutRequest = new LogoutRequest(ctx.header("authorization"));
        userService.logout(logoutRequest);
        ctx.status(200);
    }

    private void listGames(Context ctx) throws DataAccessException, UnauthorizedException {
        ListGamesRequest listGamesRequest = new ListGamesRequest(ctx.header("authorization"));
        ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);
        ctx.result(new Gson().toJson(listGamesResult));
        ctx.status(200);
    }

    private void createGame(Context ctx) throws BadRequestException, UnauthorizedException, DataAccessException {
        String authToken = ctx.header("authorization");
        String gameName = (String) new Gson().fromJson(ctx.body(), Map.class).get("gameName");
        if (gameName == null || gameName.isBlank()) {
            throw new BadRequestException("must include new game name");
        }
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        ctx.result(new Gson().toJson(createGameResult));
        ctx.status(200);
    }

    private void joinGame(Context ctx) throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        String authToken = ctx.header("authorization");
        JoinGameRequest jsonFields = new Gson().fromJson(ctx.body(), JoinGameRequest.class);
        if (!Objects.equals(jsonFields.playerColor(), "WHITE") && !Objects.equals(jsonFields.playerColor(), "BLACK")) {
            throw new BadRequestException("color must be 'WHITE' or 'BLACK'");
        }
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, jsonFields.playerColor(), jsonFields.gameID());
        gameService.joinGame(joinGameRequest);
        ctx.status(200);
    }

    private boolean isFieldBlank(String field) {
        return field == null || field.isBlank();
    }
}
