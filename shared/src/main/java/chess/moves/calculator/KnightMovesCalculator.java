package chess.moves.calculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.ArrayList;

public class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        int row = position.getRow();
        int col = position.getColumn();

        updatePossibleMoves(possibleMoves, position, new ChessPosition(row + 1, col + 2), null, piece, board);
        updatePossibleMoves(possibleMoves, position, new ChessPosition(row + 1, col - 2), null, piece, board);
        updatePossibleMoves(possibleMoves, position, new ChessPosition(row + 2, col + 1), null, piece, board);
        updatePossibleMoves(possibleMoves, position, new ChessPosition(row + 2, col - 1), null, piece, board);
        updatePossibleMoves(possibleMoves, position, new ChessPosition(row - 1, col + 2), null, piece, board);
        updatePossibleMoves(possibleMoves, position, new ChessPosition(row - 1, col - 2), null, piece, board);
        updatePossibleMoves(possibleMoves, position, new ChessPosition(row - 2, col + 1), null, piece, board);
        updatePossibleMoves(possibleMoves, position, new ChessPosition(row - 2, col - 1), null, piece, board);

        return possibleMoves;
    }
}
