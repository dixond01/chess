package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import chess.ChessPiece;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor teamTurn;
    ChessBoard gameBoard;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessBoard board = new ChessBoard(gameBoard);

    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece myPiece = gameBoard.getPiece(move.getStartPosition());
        if (!validMoves(move.getStartPosition()).contains(move) || getTeamTurn() != myPiece.getTeamColor()) {
            throw new InvalidMoveException();
        }
        gameBoard.addPiece(move.getStartPosition(), null);
        gameBoard.addPiece(move.getEndPosition(), myPiece);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        HashMap<ChessPosition, ChessPiece> enemyPositions = new HashMap<>();
        //can separate into helper methods getTeamPositions and getKingPosition
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition possiblePosition = new ChessPosition(i + 1, j + 1);
                ChessPiece piece = gameBoard.getPiece(possiblePosition);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    enemyPositions.put(possiblePosition, piece);
                } else if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = possiblePosition;
                }
            }
        }
        for (Map.Entry<ChessPosition, ChessPiece> entry : enemyPositions.entrySet()) {
            Collection<ChessMove> possibleMoves = entry.getValue().pieceMoves(gameBoard, entry.getKey());
            for (ChessMove move : possibleMoves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
