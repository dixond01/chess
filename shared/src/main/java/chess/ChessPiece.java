package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        PieceType pieceType = piece.getPieceType();

        if (pieceType == PieceType.BISHOP) {
            possibleMoves = new BishopMovesCalculator().pieceMoves(board, myPosition);
        } else if (pieceType == PieceType.ROOK) {
            possibleMoves = new RookMovesCalculator().pieceMoves(board, myPosition);
        } else if (pieceType == PieceType.QUEEN) {
            possibleMoves = new QueenMovesCalculator().pieceMoves(board, myPosition);
        } else if (pieceType == PieceType.PAWN) {
            possibleMoves = new PawnMovesCalculator().pieceMoves(board, myPosition);
        } else if (pieceType == PieceType.KING) {
            possibleMoves = new KingMovesCalculator().pieceMoves(board, myPosition);
        } else if (pieceType == PieceType.KNIGHT) {
            possibleMoves = new KnightMovesCalculator().pieceMoves(board, myPosition);
        }
        return possibleMoves;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
