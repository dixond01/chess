package model.request;

public record JoinGameRequest(String authToken, String playerColor, int gameID) {
}
