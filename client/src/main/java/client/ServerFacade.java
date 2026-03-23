package client;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData registerUser(UserData user) throws ResponseException{
        var request = buildRequest("POST", "/user", user);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException{
        var request = buildRequest("POST", "/session", new LoginRequest(username, password));
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(String auth) throws ResponseException{ // the auth is in the header somehow
        var request = buildRequest("DELETE", "/session", null, auth);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void clear() throws ResponseException{
        var request = buildRequest("DELETE", "/db", null);
        sendRequest(request);
    }

    public ListGamesResult listGames() throws ResponseException {
        var request = buildRequest("GET", "/game", null);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest buildRequest(String method, String path, Object body, String auth) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .header("Authorization", auth)
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
