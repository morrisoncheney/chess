package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.AuthData;
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

    public void clear(Context ctx) {
        Integer returned = this.service.memorySelfDestruct();
        String json = new Gson().toJson(returned);
        ctx.json(json);
    }
}
