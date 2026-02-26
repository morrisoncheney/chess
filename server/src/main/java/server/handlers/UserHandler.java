package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.UserData;

import java.util.Map;

public class UserHandler {

    public static void register(Context ctx) {
        // Convert body json to object
        UserData user = new Gson().fromJson(ctx.body(), UserData.class); // Ok, we now have an object.

        user.check();





        // Convert bodyObject back to json and send to client
//        String json = new Gson().toJson(bodyObject);
//        context.json(json);
    }

}
