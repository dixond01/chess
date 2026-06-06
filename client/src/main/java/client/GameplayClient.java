package client;

import chess.*;
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

    private ChessGame.TeamColor color;

    static final String POSITION_FORMAT_ERROR = "Error: please format piece position: <column (letter)><row (number)>";


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
                    - redraw
                    - highlight <column (letter)><row (number)>
                    - makeMove <starting column><starting row> <ending column> <ending row> <optional: promotion piece>
                    - quit
                    """;
        }
        else {
            return """
                    - help
                    - redraw
                    - highlight <column (letter)><row (number)>
                    - quit
                    """;
        }
    }

    @Override
    public String evaluateCommand(String cmd, String[] params) throws DataAccessException {
        if (participant == ParticipantType.PLAYER) {
            return switch (cmd) {
                case ("help") -> help();
                case ("redraw") -> redrawChessBoard();
                case ("highlight") -> highlightLegalMoves(params);
                case ("makemove") -> makeMove(params);
                case ("leave") -> "quit";
                default -> null;
            };
        }
        else {
            return switch (cmd) {
                case ("help") -> help();
                case ("redraw") -> redrawChessBoard();
                case ("highlight") -> highlightLegalMoves(params);
                case ("leave") -> "quit";
                default -> null;
            };
        }
    }


    //must support:
    //help
    //redraw chess board
    //leave
    //make move
    //resign
    //highlight legal moves
    private String redrawChessBoard() {
        var ui = new GameBoardUI(gameData.game(), color);
        ui.drawGame(false, null);
        return "";
    }
    private String highlightLegalMoves(String[] params){
        if (params.length < 1) {
            return "Error: please include position to highlight moves for";
        } else if (params.length > 3) {
            return POSITION_FORMAT_ERROR;
        }
        String positionAddress = params[1];
        if (params[2] != null) {
            positionAddress += params[2];
        }
        try {
            ChessPosition piecePosition = getChessPosition(positionAddress);
            var ui = new GameBoardUI(gameData.game(), color);
            ui.drawGame(true, piecePosition);

            return String.format("Highlighted possible moves for piece at position %s", positionAddress);
        } catch (DataAccessException e) {
            return e.getMessage();
        }
    }

    private String makeMove(String[] params) throws DataAccessException {
        if (gameData.game().getTeamTurn() != color) {
           return "Error: please wait for your turn";
        }

        String startString;
        String endString;
        ChessPiece.PieceType promotionPiece = null;
        if (params.length < 1) {
            return "Error:  include start and end positions";
        } else if (params.length > 5) {
            return POSITION_FORMAT_ERROR;
        } else if (params.length > 3) {
            startString = params[0] + params[1];
            endString = params[2] + params[3];
            if (params.length == 5) {
                promotionPiece = promotionFromString(params[4]);
            }
        } else if (params.length > 1) {
            startString = params[0];
            endString = params[1];
            if (params.length == 3) {
                promotionPiece = promotionFromString(params[2]);
            }
        } else {
            String param = params[0];
            if (param.length() != 4){
                return POSITION_FORMAT_ERROR;
            }
            startString = param.substring(0,2);
            endString = param.substring(2);
        }

        ChessGame game = gameData.game();
        ChessMove move = new ChessMove(getChessPosition(startString), getChessPosition(endString), promotionPiece);
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            return "Error: invalid move or promotion piece";
        }
        return "";
    }

    //implements leave
    @Override
    public void quit() {
        System.out.println("Leaving game and returning to selection screen...");
        new PostLoginClient(server).run();
    }

    //helper methods

    private ChessPosition getChessPosition(String positionAddress) throws DataAccessException {
        String outOfRangeError = "Error: column must be a-h, row must be 1-8";
        char[] cleanChars = positionAddress.replace(" ", "").toLowerCase().toCharArray();
        if (cleanChars.length != 2) {
            //does this have to be an error message? I'm confused.
            throw new DataAccessException(POSITION_FORMAT_ERROR);
        }

        char letter = cleanChars[0];
        if (!Character.isLetter(letter)) {
            throw new DataAccessException(POSITION_FORMAT_ERROR);
        }
        int col = letter - 'a' + 1;

        char number = cleanChars[1];
        if (!Character.isDigit(number)) {
            throw new DataAccessException(POSITION_FORMAT_ERROR);
        }
        int row = Character.getNumericValue(number);

        if (row < 1 || row > 8 || col < 1 || col > 8) {
            throw new DataAccessException(outOfRangeError);
        }

        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType promotionFromString(String promotionPiece) {
        promotionPiece = promotionPiece.toLowerCase();
        return switch (promotionPiece) {
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            default -> null;
        };
    }
}
