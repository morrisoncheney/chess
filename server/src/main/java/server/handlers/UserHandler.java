package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.AuthData;
import model.LoginRequest;
import model.LogoutRequest;
import model.UserData;
import server.BadRequestException;
import service.Service;


public class UserHandler {

    final private Service service;

    public UserHandler(Service service){
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

        // Convert bodyObject back to json and send to client
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

        LogoutRequest log;

        try {
            log = new LogoutRequest(ctx.headerMap().get("authToken"));
        } catch (Error e) {
            throw new BadRequestException("bad request");
        }

        service.logoutRequest(log);
        String json = new Gson().toJson("");
        ctx.json(json);
    }

    public void clear(Context ctx) {
        Integer returned = this.service.memorySelfDestruct();
        String json = new Gson().toJson(returned); // replace returned with "" for an empty response body rather than null
        ctx.json(json);
    }
}
