package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        chess.ChessGame.TeamColor color = piece.getTeamColor();
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        Collection<ChessPosition> newPositions = new ArrayList<>();

        int row = position.getRow();
        int col = position.getColumn();

        if (color == ChessGame.TeamColor.WHITE) {
            var firstPosition = new ChessPosition(row + 1, col);
            newPositions.add(firstPosition);

            if (row == 2) {
                validateStraight(newPositions, board, firstPosition, 1);
            }

            var leftDiagonal = new ChessPosition(row + 1, col - 1);
            var rightDiagonal = new ChessPosition(row + 1, col + 1);
            validateDiagonal(newPositions, board, leftDiagonal);
            validateDiagonal(newPositions, board, rightDiagonal);

        } else if (color == ChessGame.TeamColor.BLACK) {
            var firstPosition = new ChessPosition(row - 1, col);
            newPositions.add(firstPosition);

            if (row == 7) {
                validateStraight(newPositions, board, firstPosition, -1);
            }

            var leftDiagonal = new ChessPosition(row - 1, col - 1);
            var rightDiagonal = new ChessPosition(row - 1, col + 1);
            validateDiagonal(newPositions, board, leftDiagonal);
            validateDiagonal(newPositions, board, rightDiagonal);
        }


        for (ChessPosition newPosition : newPositions) {
            if ((color == ChessGame.TeamColor.WHITE && newPosition.getRow() == 8) || (color == ChessGame.TeamColor.BLACK && newPosition.getRow() == 1)) {
                pawnUpdatePossibleMoves(possibleMoves, position, newPosition, ChessPiece.PieceType.QUEEN, piece, board);
                pawnUpdatePossibleMoves(possibleMoves, position, newPosition, ChessPiece.PieceType.BISHOP, piece, board);
                pawnUpdatePossibleMoves(possibleMoves, position, newPosition, ChessPiece.PieceType.ROOK, piece, board);
                pawnUpdatePossibleMoves(possibleMoves, position, newPosition, ChessPiece.PieceType.KNIGHT, piece, board);
            } else {
                pawnUpdatePossibleMoves(possibleMoves, position, newPosition, null, piece, board);
            }
        }

        return possibleMoves;
    }

    private void validateDiagonal(Collection<ChessPosition> newPositions,
                                  ChessBoard board, ChessPosition diagonal) {
        if (!diagonal.outOfRange()) {
            ChessPiece otherPiece = board.getPiece(diagonal);
            if (otherPiece != null) {
                newPositions.add(diagonal);
            }
        }
    }

    private void validateStraight(Collection<ChessPosition> newPositions,
                                  ChessBoard board, ChessPosition firstPosition, int increment) {
        if (!firstPosition.outOfRange()) {
            ChessPiece otherPiece = board.getPiece(firstPosition);
            if (otherPiece == null) {
                newPositions.add(new ChessPosition(firstPosition.getRow() + increment, firstPosition.getColumn()));
            }
        }
    }

    private boolean pawnCanCapture(ChessPiece myPiece, ChessPiece otherPiece,
                                   ChessPosition myPosition, ChessPosition newPosition) {
        if (otherPiece == null) {
            return false;
        } else if (myPosition.getColumn() == newPosition.getColumn()) {
            return false;
        } else {
            return myPiece.getTeamColor() != otherPiece.getTeamColor();
        }
    }

    private void pawnUpdatePossibleMoves
            (Collection<ChessMove> possibleMoves,
             ChessPosition position,
             ChessPosition newPosition,
             ChessPiece.PieceType promotionPiece,
             ChessPiece piece,
             ChessBoard board) {
        if (!newPosition.outOfRange()) {
            ChessPiece otherPiece = board.getPiece(newPosition);
            if (otherPiece == null || pawnCanCapture(piece, otherPiece, position, newPosition)) {
                possibleMoves.add(new ChessMove(position, newPosition, promotionPiece));
            }
        }
    }
}
