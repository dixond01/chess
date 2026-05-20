package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import service.BadRequestException;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.request.RegisterRequest;
import service.result.RegisterResult;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    private final UserDAO userDAO = new MemoryUserDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    private final ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
    private final GameService gameService = new GameService(gameDAO, authDAO);
    private final UserService userService = new UserService(userDAO, authDAO);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .exception(BadRequestException.class, (e, ctx) -> {
                    ctx.status(400);
                    errorResult(e, ctx);
                })
                .exception(AlreadyTakenException.class, (e, ctx) -> {
                    ctx.status(403);
                    errorResult(e, ctx);
                })
                .exception(DataAccessException.class, (e, ctx) -> {
                    ctx.status(500);
                    errorResult(e, ctx);
                });


        // Register your endpoints and exception handlers here.

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
            throw new BadRequestException("Must include username, password, and email.");
        }
        RegisterResult registerResult = userService.register(registerRequest);
        ctx.result(new Gson().toJson(registerResult));
        ctx.status(200);
    }

    private boolean isFieldBlank(String field) {
        return field == null || field.isBlank();
    }
}
