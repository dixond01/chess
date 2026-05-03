package chess;
import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator{
    
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        int row = position.getRow();
        int col = position.getColumn();
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        boolean upFlag = true;
        boolean rightFlag= true;
        boolean leftFlag = true;
        boolean bottomFlag = true;

        for (int i = 1; i < 8; i++) {
            if (upFlag) {
                var newPosition = new ChessPosition(row + i, col);
                updatePossibleMoves(possibleMoves, position, newPosition, null, piece, board);
                upFlag = updateFlag(board, newPosition);
            }
            if (rightFlag) {
                var newPosition = new ChessPosition(row, col + i);
                updatePossibleMoves(possibleMoves, position, newPosition, null, piece, board);
                rightFlag = updateFlag(board, newPosition);
            }
            if (leftFlag) {
                var newPosition = new ChessPosition(row, col - i);
                updatePossibleMoves(possibleMoves, position, newPosition, null, piece, board);
                leftFlag = updateFlag(board, newPosition);
            }
            if (bottomFlag) {
                var newPosition = new ChessPosition(row - i, col);
                updatePossibleMoves(possibleMoves, position, newPosition, null, piece, board);
                bottomFlag = updateFlag(board, newPosition);
            }
        }
        return possibleMoves;
    }
}
