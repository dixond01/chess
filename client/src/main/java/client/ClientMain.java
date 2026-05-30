package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) {
        String serverPort = "8080";
        if (args.length == 1) {
            serverPort = args[0];
        }

        try {
            new PreLoginClient(serverPort).run();
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}
