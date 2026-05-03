package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        int row = position.getRow();
        int col = position.getColumn();
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        boolean topLeftFlag = true;
        boolean topRightFlag= true;
        boolean bottomLeftFlag = true;
        boolean bottomRightFlag = true;

        for (int i = 1; i < 8; i++) {
            if (topLeftFlag) {
                var newPosition = new ChessPosition(row + i, col - i);
                updatePossibleMoves(possibleMoves, position, newPosition, null, piece, board);
                topLeftFlag = updateFlag(board, newPosition);
            }
            if (topRightFlag) {
                var newPosition = new ChessPosition(row + i, col + i);
                updatePossibleMoves(possibleMoves, position, newPosition, null, piece, board);
                topRightFlag = updateFlag(board, newPosition);
            }
            if (bottomLeftFlag) {
                var newPosition = new ChessPosition(row - i, col - i);
                updatePossibleMoves(possibleMoves, position, newPosition, null, piece, board);
                bottomLeftFlag = updateFlag(board, newPosition);
            }
            if (bottomRightFlag) {
                var newPosition = new ChessPosition(row - i, col + i);
                updatePossibleMoves(possibleMoves, position, newPosition, null, piece, board);
                bottomRightFlag = updateFlag(board, newPosition);
            }
        }
        return possibleMoves;
    }
}
