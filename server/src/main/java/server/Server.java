package server;

import io.javalin.*;
import io.javalin.http.Context;
import server.handlers.UserHandler;

import java.util.Map;


public class Server {

    private final Javalin javalin;

    public Server() {

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
//                .delete("/db", this::clear)
//                .post("/session",this::login)
//                .delete("/session",this::logout)
//                .get("/game",this::getGames)
//                .post("/game",this::createGame)
//                .put("/game",this::joinGame)
                    ;


//        javalin = Javalin.create(config -> {
//                    config.staticFiles.add("/web", io.javalin.http.staticfiles.Location.CLASSPATH);
//                })
//                .post("/user", this::register);

        addExceptions();

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context context){
        UserHandler.register(context);
    }

    private void addExceptions(){
        javalin.exception(IllegalArgumentException.class, (e, context) -> {
            context.status(400); // 400 means "Bad Request" (the user messed up)
            context.json(Map.of("Error", e.getMessage()));
        });
        javalin.exception(BadRequestException.class, (e, context) -> {
            context.status(403); // 400 means "Bad Request" (the user messed up)
            context.json(Map.of("Error", e.getMessage()));
        });



    }
}
