package chess.moves.calculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        ChessGame.TeamColor color = piece.getTeamColor();
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        Collection<ChessPosition> possiblePositions = new ArrayList<>();

        validateStraight(possiblePositions, position, board, color);
        validateDiagonals(possiblePositions, position, board, color);

        for (ChessPosition possiblePosition : possiblePositions) {
            if ((color == ChessGame.TeamColor.WHITE && possiblePosition.getRow() == 8)
                    || (color == ChessGame.TeamColor.BLACK && possiblePosition.getRow() == 1)) {
                pawnUpdatePossibleMoves(possibleMoves, position, possiblePosition, ChessPiece.PieceType.QUEEN, piece, board);
                pawnUpdatePossibleMoves(possibleMoves, position, possiblePosition, ChessPiece.PieceType.BISHOP, piece, board);
                pawnUpdatePossibleMoves(possibleMoves, position, possiblePosition, ChessPiece.PieceType.ROOK, piece, board);
                pawnUpdatePossibleMoves(possibleMoves, position, possiblePosition, ChessPiece.PieceType.KNIGHT, piece, board);
            } else {
                pawnUpdatePossibleMoves(possibleMoves, position, possiblePosition, null, piece, board);
            }
        }

        return possibleMoves;
    }

    private void validateStraight(Collection<ChessPosition> possiblePositions, ChessPosition position, ChessBoard board, ChessGame.TeamColor color) {
        int increment = 1;
        int startingRow = 2;

        if (ChessGame.TeamColor.BLACK == color) {
            increment = -1;
            startingRow = 7;
        }

        var firstPosition = new ChessPosition(position.getRow() + increment, position.getColumn());
        possiblePositions.add(firstPosition);

        if (position.getRow() == startingRow && !firstPosition.outOfRange()) {
            ChessPiece otherPiece = board.getPiece(firstPosition);
            if (otherPiece == null) {
                possiblePositions.add(new ChessPosition(firstPosition.getRow() + increment, firstPosition.getColumn()));
            }
        }
    }

    private void validateDiagonals(Collection<ChessPosition> possiblePositions, ChessPosition position,
                                   ChessBoard board, ChessGame.TeamColor color) {
        int increment = 1;
        if (ChessGame.TeamColor.BLACK == color) {
            increment = -1;
        }
        ChessPosition firstDiagonal = new ChessPosition(position.getRow() + increment, position.getColumn() - 1);
        ChessPosition secondDiagonal = new ChessPosition(position.getRow() + increment, position.getColumn() + 1);
        validateDiagonal(possiblePositions, firstDiagonal, position, board);
        validateDiagonal(possiblePositions, secondDiagonal, position, board);
    }

    private void validateDiagonal(Collection<ChessPosition> possiblePositions, ChessPosition diagonal,
                                  ChessPosition position, ChessBoard board) {
        if (!diagonal.outOfRange()) {
            ChessPiece myPiece = board.getPiece(position);
            ChessPiece otherPiece = board.getPiece(diagonal);
            if (pawnCanCapture(myPiece, otherPiece, position, diagonal)) {
                possiblePositions.add(diagonal);
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
