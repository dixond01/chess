package chess;

import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);

    default boolean canCapture(ChessPiece myPiece, ChessPiece otherPiece){
        if (otherPiece == null) {
            return false;
        }
        else {
            return myPiece.getTeamColor() != otherPiece.getTeamColor();
        }
    }

    default void updatePossibleMoves
            (Collection<ChessMove> possibleMoves,
             ChessPosition position,
             ChessPosition newPosition,
             ChessPiece.PieceType promotionPiece,
             ChessPiece piece,
             ChessBoard board) {
        if (!newPosition.outOfRange()) {
            ChessPiece otherPiece = board.getPiece(newPosition);
            if (otherPiece == null || canCapture(piece, otherPiece)) {
                possibleMoves.add(new ChessMove(position, newPosition, promotionPiece));
            }
        }
    }

    default boolean updateFlag(ChessBoard board, ChessPosition newPosition) {
        if (newPosition.outOfRange()) {
            return false;
        }
        else {
            ChessPiece otherPiece = board.getPiece(newPosition);
            return otherPiece == null;
        }
    }



}
