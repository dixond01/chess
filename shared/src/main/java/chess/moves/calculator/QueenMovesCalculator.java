package chess.moves.calculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        Collection<ChessMove> diagonalMoves = new BishopMovesCalculator().pieceMoves(board, position);
        Collection<ChessMove> straightMoves = new RookMovesCalculator().pieceMoves(board, position);
        possibleMoves.addAll(diagonalMoves);
        possibleMoves.addAll(straightMoves);
        return possibleMoves;
    }
}
