package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    @Override
    public String toString() {
        return String.format("Name: %s, White Player: %s, Black Player: %s", gameName, whiteUsername, blackUsername);
    }
}
