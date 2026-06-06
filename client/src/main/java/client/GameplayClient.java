package client;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.ServerMessageObserver;
import client.websocket.WebSocketFacade;
import model.exception.DataAccessException;
import model.GameData;
import ui.GameBoardUI;
import websocket.messages.ServerMessage;

import java.util.HashMap;

public class GameplayClient implements Client, ServerMessageObserver {
    private final ParticipantType participant;
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private GameData gameData;

    private ChessGame.TeamColor color;

    public GameplayClient(ServerFacade server, GameData gameData, ParticipantType participant, ChessGame.TeamColor color) throws DataAccessException {
        this.server = server;
        this.gameData = gameData;
        this.participant = participant;
        this.ws = new WebSocketFacade(server.getServerUrl(), this);
        this.color = color;
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


    //must support:
    //help
    //redraw chess board
    //leave
    //make move
    //resign
    //highlight legal moves
    private void redrawChessBoard() {
        var ui = new GameBoardUI(gameData.game(), color);
        ui.drawGame(false, null);
    }
    private void highlightLegalMoves(String positionAddress) throws DataAccessException {
        var ui = new GameBoardUI(gameData.game(), color);
        try {
            ChessPosition piecePosition = getChessPosition(positionAddress);
            ui.drawGame(true, piecePosition);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
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

    //implements leave
    @Override
    public void quit() {
        System.out.println("Leaving game and returning to selection screen...");
        new PostLoginClient(server).run();
    }

    private ChessPosition getChessPosition(String positionAddress) throws DataAccessException {
        String formatError = "Error: please format piece position: <column (letter)><row (number)>";
        String outOfRangeError = "Error: column must be a-h, row must be 1-8";
        char[] cleanChars = positionAddress.replace(" ", "").toLowerCase().toCharArray();
        if (cleanChars.length != 2) {
            //does this have to be an error message? I'm confused.
            throw new DataAccessException(formatError);
        }

        char letter = cleanChars[0];
        if (!Character.isLetter(letter)) {
            throw new DataAccessException(formatError);
        }
        int col = letter - 'a' + 1;

        char number = cleanChars[1];
        if (!Character.isDigit(number)) {
            throw new DataAccessException(formatError);
        }
        int row = Character.getNumericValue(number);

        if (row < 1 || row > 8 || col < 1 || col > 8) {
            throw new DataAccessException(outOfRangeError);
        }

        return new ChessPosition(row, col);
    }
}
