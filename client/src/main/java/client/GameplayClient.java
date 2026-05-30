package client;

import model.DataAccessException;
import model.GameData;
import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.BLUE;

public class GameplayClient implements Client {
    private ParticipantType participant;
    private final ServerFacade server;
    private GameData game;

    public GameplayClient(ServerFacade server, GameData game, ParticipantType participant) {
        this.server = server;
        this.game = game;
        this.participant = participant;
    }

    @Override
    public String startMessage() {
        return String.format("Welcome! %s", game.toString());
    }

    @Override
    public String help() {
        printWhiteBoard();
        printBlackBoard();
        if (participant == ParticipantType.PLAYER) {
            return """
                    - help
                    - quit
                    """;
        }
        else {
            return """
                    - help
                    - quit
                    """;
        }
    }

    @Override
    public String evaluateCommand(String cmd, String[] params) throws DataAccessException {
        if (participant == ParticipantType.PLAYER) {
            return switch (cmd) {
                case ("help") -> help();
                default -> null;
            };
        }
        else {
            return switch (cmd) {
                case ("help") -> help();
                default -> null;
            };
        }
    }

    private void printWhiteBoard() { }
    private void printBlackBoard() { }
}
