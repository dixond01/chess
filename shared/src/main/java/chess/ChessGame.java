package chess;

import java.util.*;

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
        this.teamTurn = TeamColor.WHITE;
        this.gameBoard = new ChessBoard();
        gameBoard.resetBoard();
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
        ChessPiece myPiece = gameBoard.getPiece(startPosition);
        if (myPiece == null) {
            return null;
        }
        TeamColor myColor = myPiece.getTeamColor();
        Collection<ChessMove> possibleMoves = myPiece.pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : possibleMoves) {
            ChessBoard testBoard = new ChessBoard(gameBoard);
            testBoard.addPiece(move.getEndPosition(), myPiece);
            testBoard.addPiece(startPosition, null);
            if (!isKingInDanger(myColor, testBoard)) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    public boolean hasValidMoves(TeamColor teamColor) {
        HashMap<ChessPosition, ChessPiece> myPositions = (HashMap<ChessPosition, ChessPiece>) getPiecePositions(teamColor);
        for (ChessPosition position : myPositions.keySet()) {
            Collection<ChessMove> validMoves = validMoves(position);
            if (validMoves != null && !validMoves.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece myPiece = gameBoard.getPiece(move.getStartPosition());
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if (moves == null || !moves.contains(move)
                || getTeamTurn() != myPiece.getTeamColor()) {
            throw new InvalidMoveException();
        }
        if (move.getPromotionPiece() != null) {
            myPiece = new ChessPiece(myPiece.getTeamColor(), move.getPromotionPiece());
        }
        gameBoard.addPiece(move.getStartPosition(), null);
        gameBoard.addPiece(move.getEndPosition(), myPiece);
        setTeamTurn(getEnemyColor(teamTurn));
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isKingInDanger(teamColor, gameBoard);

    }

    public TeamColor getEnemyColor(TeamColor myColor) {
        TeamColor enemyColor = TeamColor.BLACK;
        if (myColor == TeamColor.BLACK) {
            enemyColor = TeamColor.WHITE;
        }
        return enemyColor;
    }

    public Map<ChessPosition, ChessPiece> getPiecePositions(TeamColor teamColor) {
        HashMap<ChessPosition, ChessPiece> piecePositions = new HashMap<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition possiblePosition = new ChessPosition(i + 1, j + 1);
                ChessPiece piece = gameBoard.getPiece(possiblePosition);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    piecePositions.put(possiblePosition, piece);
                }
            }
        }
        return piecePositions;
    }

    public ChessPosition getKingPosition(ChessBoard board, TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition possiblePosition = new ChessPosition(i + 1, j + 1);
                ChessPiece piece = board.getPiece(possiblePosition);
                if (piece != null && piece.getTeamColor() == teamColor
                        && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = possiblePosition;
                }
            }
        }
        return kingPosition;
    }


    public boolean isKingInDanger(TeamColor teamColor, ChessBoard board) {
        TeamColor enemyColor = getEnemyColor(teamColor);
        HashMap<ChessPosition, ChessPiece> enemyPositions = (HashMap<ChessPosition, ChessPiece>) getPiecePositions(enemyColor);

        ChessPosition kingPosition = getKingPosition(board, teamColor);
        for (Map.Entry<ChessPosition, ChessPiece> entry : enemyPositions.entrySet()) {
            Collection<ChessMove> possibleMoves = entry.getValue().pieceMoves(board, entry.getKey());
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
        return isInCheck(teamColor) && !hasValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !hasValidMoves(teamColor);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, gameBoard);
    }
}
