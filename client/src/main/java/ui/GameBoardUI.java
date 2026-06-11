package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.*;

public class GameBoardUI {

    private static final int CHESS_BOARD_SIZE_IN_SQUARES = 8;
    private final ChessGame game;
    private final ChessGame.TeamColor viewerColor;

    public GameBoardUI(ChessGame game, ChessGame.TeamColor viewerColor) {
        this.game = game;
        this.viewerColor = viewerColor;
    }

    public void drawGame(Boolean shouldHighlight, ChessPosition highlightPosition) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        if (viewerColor == ChessGame.TeamColor.WHITE) {
            printHeader(out, ChessGame.TeamColor.WHITE);
            printRows(out, ChessGame.TeamColor.WHITE, shouldHighlight, highlightPosition);
            printHeader(out, ChessGame.TeamColor.WHITE);
        } else {
            printHeader(out, ChessGame.TeamColor.BLACK);
            printRows(out, ChessGame.TeamColor.BLACK, shouldHighlight, highlightPosition);
            printHeader(out, ChessGame.TeamColor.BLACK);
        }

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.println();
    }

    public boolean[][] getHighlightArray(ChessPosition position) {
        Collection<ChessMove> possibleMoves;
        if (game.getBoard().getPiece(position) != null) {
            possibleMoves = game.validMoves(position);
        } else {
            return new boolean[8][8];
        }

        Collection<ChessPosition> endPositions = new ArrayList<>();
        for (ChessMove move : possibleMoves) {
            endPositions.add(move.getEndPosition());
        }

        boolean[][] highlightArray = new boolean[8][8];
        for (ChessPosition endPosition : endPositions) {
            highlightArray[endPosition.getRow() - 1][endPosition.getColumn() - 1] = true;
        }

        return highlightArray;
    }


    private void printHeader(PrintStream out, ChessGame.TeamColor color) {
        setBorderColor(out);
        printEmptySpace(out);
        List<String> letters = new ArrayList<>(List.of("a","b","c","d","e","f","g","h"));
        if (color == ChessGame.TeamColor.BLACK) {
            letters = letters.reversed();
        }
        for (String letter : letters) {
            out.print(pad(letter));
        }
        printEmptySpace(out);
        printEndOfRow(out);
    }

    private void printRows(PrintStream out, ChessGame.TeamColor color, boolean shouldHighlight, ChessPosition highlightPosition) {
        boolean[][] highlightArray = new boolean[8][8];
        if (shouldHighlight) {
            highlightArray = getHighlightArray(highlightPosition);
        }
        List<String> numbers = new ArrayList<>(List.of("1","2","3","4","5","6","7","8"));
        ChessPiece[][] gameBoard = game.getBoard().getSquares();
        if (color == ChessGame.TeamColor.BLACK) {
            numbers = numbers.reversed();
            gameBoard = reverseSquares(gameBoard);
            highlightArray = reverseHighlightArray(highlightArray);

        }
        for (int rowIndex = CHESS_BOARD_SIZE_IN_SQUARES - 1; rowIndex >= 0; rowIndex--) {
            setBorderColor(out);
            out.print(pad(numbers.get(rowIndex)));

            printChessRow(out, gameBoard[rowIndex], rowIndex, highlightArray);

            setBorderColor(out);
            out.print(pad(numbers.get(rowIndex)));

            resetColor(out);
            printEndOfRow(out);
        }
    }

    private void printChessRow(PrintStream out, ChessPiece[] row, int rowIndex, boolean[][] highlightArray) {
        for (int i = 0; i < CHESS_BOARD_SIZE_IN_SQUARES; i++) {
            printSquare(out, row, rowIndex, i, highlightArray);
        }
    }

    private void printSquare(PrintStream out, ChessPiece[] row, int rowIndex, int colIndex, boolean[][] highlightArray) {
        if (highlightArray[rowIndex][colIndex]) {
            setHighlightBG(out);
        } else if (rowIndex % 2 == 0) {
            if (colIndex % 2 == 0) {
                setDarkBG(out);
            }
            else {
                setLightBG(out);
            }
        } else {
            if (colIndex % 2 == 0) {
                setLightBG(out);
            } else {
                setDarkBG(out);
            }
        }
        ChessPiece piece = row[colIndex];
        if (piece == null) {
            printEmptySpace(out);
        } else {
            out.print(getColorAsString(piece.getTeamColor()));
            String pieceType = getPieceTypeAsString(piece.getPieceType());

            out.print(pad(pieceType));
        }
    }

    private String getPieceTypeAsString(ChessPiece.PieceType type) {
        return switch (type) {
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "P";
        };
    }

    private String getColorAsString(ChessGame.TeamColor color) {
        String colorString = SET_TEXT_COLOR_WHITE;
        if (color == ChessGame.TeamColor.BLACK) {
            colorString = SET_TEXT_COLOR_BLACK;
        }
        return colorString;
    }

    private void printEmptySpace(PrintStream out) {
        out.print(pad(" "));
    }
    private String pad(String character) {
        return String.format(" %s ", character);
    }

    private void resetColor(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private void setBorderColor(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private void setLightBG(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT);
    }

    private void setDarkBG(PrintStream out) {
        out.print(SET_BG_COLOR_DARK);
    }

    private void setHighlightBG(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
    }

    private void printEndOfRow(PrintStream out) {
        resetColor(out);
        out.println();
    }

    private ChessPiece[][] reverseSquares(ChessPiece[][] squares) {
        List<ChessPiece[]> rowsReversed = new ArrayList<>();
        for (ChessPiece[] row : squares) {
            List<ChessPiece> rowAsList = new ArrayList<>(Arrays.asList(row));
            rowsReversed.add(rowAsList.reversed().toArray(new ChessPiece[0]));
        }
        return rowsReversed.reversed().toArray(new ChessPiece[8][0]);
    }

    private boolean[][] reverseHighlightArray(boolean[][] highlightArray) {
        boolean[][] reversed = new boolean[8][8];

        for (int i = 0; i < 8; i++) {
            boolean[] originalRow = highlightArray[7 - i];
            for (int j = 0; j < 8; j++) {
                reversed[i][j] = originalRow[7 - j];
            }
        }

        return reversed;
    }


}
