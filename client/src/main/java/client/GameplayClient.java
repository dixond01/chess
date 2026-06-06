package client;

import chess.ChessGame;
import client.websocket.ServerMessageObserver;
import client.websocket.WebSocketFacade;
import model.exception.DataAccessException;
import model.GameData;
import ui.GameBoardUI;
import websocket.messages.ServerMessage;

public class GameplayClient implements Client, ServerMessageObserver {
    private final ParticipantType participant;
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private GameData gameData;

    //could put a color field here if needed

    public GameplayClient(ServerFacade server, GameData gameData, ParticipantType participant) throws DataAccessException {
        this.server = server;
        this.gameData = gameData;
        this.participant = participant;
        this.ws = new WebSocketFacade(server.getServerUrl(), this);
        //petshop creates the websocket here
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()) {
            case ERROR -> System.out.println(serverMessage.toString());
            case NOTIFICATION -> System.out.println(serverMessage.toString());
            case LOAD_GAME -> redrawChessBoard();
        }
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


    //must support
    //help
    //redraw chess board
    //leave
    //make move
    //resign
    //highlight legal moves
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
