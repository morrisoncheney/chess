package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.MySqlDataAccess;
import exception.ResponseException;
import io.javalin.http.Context;
import model.GameData;
import websocket.messages.Notification;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import service.Service;
import websocket.commands.UserGameCommand;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    MySqlDataAccess dataAccess = new MySqlDataAccess();
    private final Service service = new Service(dataAccess);
    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws Exception {
        try {
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (action.getCommandType()) {
                case CONNECT -> enter(action.getAuthToken(), action.getGameID(), action.get ctx.session);
                case MAKE_MOVE -> makeMove(action.getAuthToken(), action.getGameID(), action.getMove(), ctx.session);
                case LEAVE -> exit(action.getAuthToken(), ctx.session);
                case RESIGN -> resign(action.getAuthToken(), action.getGameID(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void enter(String authToken, Session session) throws IOException {
        connections.add(session);

        AuthData auth = authenticate(authToken);
        dataAccess.replaceUser();
        var message = String.format("%s is connected.", auth.username());
        var notification = new Notification(Notification.Type.ENTER, message);
        connections.broadcastToGame(authToken, notification);
        // THIS LINE NEEDS TO CHANGE TO ONLY NOTIFY ANOTHER USER IN THAT GAME IF THERE IS ONE
    }

    private void exit(String authToken, Session session) throws IOException {
        AuthData authData = authenticate(authToken);
        var message = String.format("%s left the game", authData.username());
//        var notification = new Notification(Notification.Type.EXIT, message);
//        connections.broadcast(session, notification); // yeah we need to change the broadcast here
        connections.remove(session);
    }

    public void makeMove(String authToken, int gameID, ChessMove move, Session session) throws InvalidMoveException {
        AuthData auth = authenticate(authToken);
        GameData gameData = dataAccess.getGame(gameID);
        ChessGame game = gameData.game();
        game.makeMove(move);
        dataAccess.updateGame(game, gameID);
        try {

//            var message = String.format("%s joins %d", auth.username(), gameID);
//            var notification = new Notification(Notification.Type.MOVE, message);
            // instead of notifying users, we want to actually implement the moves.
//            connections.broadcast(null, notification);
            // the above line needs to change. we don't want to notify everyone.
        } catch (Exception ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
            // this exception needs to be changed to throw the right error code number because
            // my code doesn't actually look at the response exceptions error types.
        }
    }

    private void resign(String authToken, Session session) throws IOException {
        // this is a placeholder
        AuthData authData = authenticate(authToken);
        var message = String.format("%s resigned the game.", authData.username());
        var notification = new Notification(Notification.Type.EXIT, message);
        connections.broadcast(session, notification); // inform other user of win, and curr user of loss
        // delete the game from the db as well probably too.
        connections.remove(session);
    }

    public AuthData authenticate(String auth) {
        return service.authenticate(auth);
    }
}