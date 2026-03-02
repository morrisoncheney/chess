package server.handlers;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.http.Context;
import model.*;
import server.BadRequestException;
import service.Service;

import java.util.ArrayList;


public class Handler {

    final private Service service;

    public Handler(Service service){
        this.service = service;
    }



    public void register(Context ctx) {

        UserData user;

        try {
            user = new Gson().fromJson(ctx.body(), UserData.class);
        } catch (Error e) {
            throw new BadRequestException("bad request");
        }

        AuthData auth = service.registerRequest(user);

        String json = new Gson().toJson(auth);
        ctx.json(json);
    }

    public void login(Context ctx) {

        LoginRequest log;

        try {
            log = new Gson().fromJson(ctx.body(), LoginRequest.class);
        } catch (Error e) {
            throw new BadRequestException("bad request");
        }

        AuthData auth = service.loginRequest(log);

        String json = new Gson().toJson(auth);
        ctx.json(json);
    }

    public void logout(Context ctx) {

        String auth = getAuth(ctx);

        service.logoutRequest(auth);

        String json = new Gson().toJson(new JsonObject());
        ctx.json(json);
    }

    public void createGame(Context ctx) {
        authenticate(ctx);

        String gameName = gameNameGetter(ctx);

        Integer gameID = service.createGame(gameName);

        CreateGameResult res = new CreateGameResult(gameID);

        String json = new Gson().toJson(res);
        ctx.json(json);
    }

    public void getGames(Context ctx) {
        authenticate(ctx);

        ArrayList<GameDataListItem> gamesList = service.getGameList();

        ctx.json(new Gson().toJson(new ListGamesResult(gamesList)));
    }

    public void joinGame(Context ctx) {
        AuthData auth = authenticate(ctx);
        JoinGameRequest request;
        try {
            request = new Gson().fromJson(ctx.body(), JoinGameRequest.class);
        } catch (Error e) {
            throw new BadRequestException("bad request");
        }

        request.check();

        service.joinGame(request, auth.username());

        String json = new Gson().toJson(new JsonObject());
        ctx.json(json);
    }

    private String gameNameGetter(Context ctx) {
        CreateGameRequest gameNameHolder;

        try {
            gameNameHolder = new Gson().fromJson(ctx.body(), CreateGameRequest.class);
        } catch (Error e) {
            throw new BadRequestException("bad request");
        }

        gameNameHolder.check();

        return gameNameHolder.gameName();
    }

    private String getAuth(Context ctx) {
        String auth;

        try {
            auth = ctx.header("Authorization");
        } catch (Error e) {
            throw new BadRequestException("bad request");
        }

        return auth;
    }

    private AuthData authenticate(Context ctx) {
        String auth = getAuth(ctx);

        return service.authenticate(auth);
    }

    public void clear(Context ctx) {
        Integer returned = this.service.memorySelfDestruct();
        String json = new Gson().toJson(returned); // replace returned with "" for an empty response body rather than null
        ctx.json(json);
    }


}
