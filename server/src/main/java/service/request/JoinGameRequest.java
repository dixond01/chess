package service.request;
//may need to have playerColor be an enum, from shared?
public record JoinGameRequest(String authToken, String playerColor, int gameID) {
}
