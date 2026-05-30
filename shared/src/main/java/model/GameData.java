package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    @Override
    public String toString() {
        String whitePlayer = whiteUsername;
        String blackPlayer = blackUsername;
        if (whitePlayer == null) {
            whitePlayer = "none";
        }
        if (blackPlayer == null) {
            blackPlayer = "none";
        }
        return String.format("Name: %s, White Player: %s, Black Player: %s", gameName, whitePlayer, blackPlayer);
    }
}
