package client;

import chess.ChessGame;
import model.DataAccessException;
import model.GameData;
import ui.GameBoardUI;

public class GameplayClient implements Client {
    private final ParticipantType participant;
    private final ServerFacade server;
    private final GameData gameData;

    public GameplayClient(ServerFacade server, GameData gameData, ParticipantType participant) {
        this.server = server;
        this.gameData = gameData;
        this.participant = participant;
    }

    @Override
    public String startMessage() {
        return String.format("Welcome! %s", gameData.toString());
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

    @Override
    public void quit() {
        System.out.println("Leaving game and returning to selection screen...");
        new PostLoginClient(server).run();
    }

    private void printWhiteBoard() {
        var ui = new GameBoardUI(gameData.game(), ChessGame.TeamColor.WHITE);
        ui.drawGame();
    }
    private void printBlackBoard() {
        var ui = new GameBoardUI(gameData.game(), ChessGame.TeamColor.BLACK);
        ui.drawGame();
    }
}
