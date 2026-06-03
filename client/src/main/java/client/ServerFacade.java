package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.DataAccessException;
import service.request.*;
import service.result.*;

import javax.xml.crypto.Data;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    private String authToken = null;

    private String username = null;

    public ServerFacade(String port) {
        serverUrl = String.format("http://localhost:%s", port);
    }

    public void clear() throws DataAccessException {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        var request = buildRequest("POST", "/user", registerRequest, null);
        var response = sendRequest(request);
        RegisterResult registerResult = handleResponse(response, RegisterResult.class);
        if (registerResult != null) {
            if (registerResult.authToken() != null) {
                this.authToken = registerResult.authToken();
            }
            if (registerResult.username() != null) {
                this.username = registerResult.username();
            }
        }
        return registerResult;
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        var request = buildRequest("POST","/session", loginRequest, null);
        var response = sendRequest(request);
        LoginResult loginResult = handleResponse(response, LoginResult.class);
        if (loginResult != null) {
            if (loginResult.authToken() != null) {
                this.authToken = loginResult.authToken();
            }
            if (loginResult.username() != null) {
                this.username = loginResult.username();
            }
        }
        return loginResult;
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        var request = buildRequest( "DELETE", "/session", null, logoutRequest.authToken());
        var response = sendRequest(request);
        handleResponse(response, null);
        this.authToken = null;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        var request = buildRequest("GET", "/game", null, listGamesRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        var request = buildRequest("POST", "/game", createGameRequest, createGameRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        var request = buildRequest("PUT", "/game", joinGameRequest, joinGameRequest.authToken());
        var response = sendRequest(request);
        handleResponse(response, null);
    }


    private HttpRequest buildRequest(String method, String path, Object body, String authHeader) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        addRequestHeader(request, body, authHeader);
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private void addRequestHeader(HttpRequest.Builder request, Object body, String authHeader) {
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authHeader != null) {
            request.setHeader("Authorization", authHeader);
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new DataAccessException();
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                throw new DataAccessException(json.get("message").getAsString());
            }

            throw new DataAccessException("Error: something went wrong.");
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
