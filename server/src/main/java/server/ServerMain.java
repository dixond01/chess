package server;

import dataaccess.*;
import service.ClearService;
import service.GameService;
import service.UserService;

public class ServerMain {
    public static void main(String[] args) {
        try {
            var port = 8080;

            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            AuthDAO authDAO = new SQLAuthDAO();
            GameDAO gameDAO = new SQLGameDAO();
            UserDAO userDAO = new SQLUserDAO();

            var clearService = new ClearService(userDAO, gameDAO, authDAO);
            var gameService = new GameService(gameDAO, authDAO);
            var userService = new UserService(userDAO, authDAO);

            var server = new Server(clearService, gameService, userService).run(port);
            port = server;
            System.out.printf("♕ 240 Chess Server started on port %d", port);
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        }
}
