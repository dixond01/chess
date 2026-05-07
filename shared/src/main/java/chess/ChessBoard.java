package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {

        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        resetBackline(ChessGame.TeamColor.WHITE);
        resetFrontline(ChessGame.TeamColor.WHITE);
        resetBackline(ChessGame.TeamColor.BLACK);
        resetFrontline(ChessGame.TeamColor.BLACK);
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = null;
            }
        }
    }
    private void resetBackline(ChessGame.TeamColor color) {
        int row = 0;
        if (color == ChessGame.TeamColor.BLACK) {
            row = 7;
        }
        squares[row][0] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
        squares[row][1] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        squares[row][2] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        squares[row][3] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
        squares[row][4] = new ChessPiece(color, ChessPiece.PieceType.KING);
        squares[row][5] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        squares[row][6] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        squares[row][7] = new ChessPiece(color, ChessPiece.PieceType.ROOK);

    }
    private void resetFrontline(ChessGame.TeamColor color) {
        int row = 1;
        if (color == ChessGame.TeamColor.BLACK) {
            row = 6;
        }
        for (int i = 0; i < 8; i++) {
            squares[row][i] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    ChessBoard (ChessBoard other) {
        ChessPiece[][] newSquares = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                newSquares[i][j] = new ChessPiece(other.squares[i][j]);
            }
        }
        this.squares = newSquares;
    }
}
