package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import server.handlers.UserHandler;
import service.Service;

import java.util.Map;


public class Server {

    private final Javalin javalin;

    private final UserHandler userHandler = new UserHandler(new Service(new MemoryDataAccess()));

    // add other handler types in here as needed

    public Server() {

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .delete("/db", this::clear)
//                .post("/session",this::login)
//                .delete("/session",this::logout)
//                .get("/game",this::getGames)
//                .post("/game",this::createGame)
//                .put("/game",this::joinGame)
                .error(404, this::notFound)
                    ;

        addExceptions();

    }

    private void notFound(Context context){
        Error e = new Error("File not found");
        context.status(404);
        context.json(new Gson().toJson(Map.of("Error:", e.getMessage())));

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context context){
        this.userHandler.register(context);
    }

    private void clear(Context context) {
        this.userHandler.clear(context);
    }

    private void addExceptions(){

        javalin.exception(BadRequestException.class, (e, context) -> {
            context.status(400);
            context.json(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        });

        javalin.exception(UnauthorizedResponse.class, (e, context) -> {
            context.status(401);
            context.json(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        });

        javalin.exception(ForbiddenResponse.class, (e, context) -> {
            context.status(403);
            context.json(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        });

        javalin.exception(Exception.class, (e, context) -> {
            context.status(500);
            context.json(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        });

    }


}
