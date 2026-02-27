package server.handlers;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import io.javalin.http.Context;
import model.UserData;
import service.Service;

import java.util.Map;

public class UserHandler {

    final private Service service;

    public UserHandler(Service service){
        this.service = service;
    }



    public void register(Context ctx) {
        // Convert body json to object
        UserData user = new Gson().fromJson(ctx.body(), UserData.class); // Ok, we now have an object.





        // Convert bodyObject back to json and send to client
//        String json = new Gson().toJson(bodyObject);
//        context.json(json);
    }

}
