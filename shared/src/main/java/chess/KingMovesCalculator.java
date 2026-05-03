package chess;
import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        updatePossibleMoves(possibleMoves, position, new ChessPosition(row + 1, col), null, piece, board);
        updatePossibleMoves(possibleMoves, position, new ChessPosition(row + 1, col + 1), null, piece, board);
        updatePossibleMoves(possibleMoves, position, new ChessPosition(row + 1, col - 1), null, piece, board);
        updatePossibleMoves(possibleMoves, position, new ChessPosition(row, col + 1), null, piece, board);
        updatePossibleMoves(possibleMoves, position, new ChessPosition(row, col - 1), null, piece, board);
        updatePossibleMoves(possibleMoves, position, new ChessPosition(row - 1, col), null, piece, board);
        updatePossibleMoves(possibleMoves, position, new ChessPosition(row - 1, col + 1), null, piece, board);
        updatePossibleMoves(possibleMoves, position, new ChessPosition(row - 1, col - 1), null, piece, board);

        return possibleMoves;
    }
}
