package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.MySqlDataAccess;
import exception.ResponseException;
import io.javalin.http.Context;
import model.BadRequestException;
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
import websocket.messages.ServerMessage;

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
        UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> enter(action.getAuthToken(), action.getGameID(), action.getColor());
            case MAKE_MOVE -> makeMove(action.getAuthToken(), action.getGameID(),
                                                                            action.getColor(), action.getMove());
            case LEAVE -> exit(action.getAuthToken(), action.getGameID(), action.getColor());
            case RESIGN -> resign(action.getAuthToken(), action.getGameID(), action.getColor());
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void enter(String authToken, int gameID, ChessGame.TeamColor color) throws IOException {
        connections.add(new Connection(gameID));

        AuthData auth = authenticate(authToken);
        GameData game = dataAccess.getGame(gameID);
        if ( (auth.username().equals(game.whiteUsername()) && color == ChessGame.TeamColor.WHITE) ||
             (auth.username().equals(game.blackUsername()) && color == ChessGame.TeamColor.BLACK) ) {

        } else {
            dataAccess.replaceUser(color, auth.username(), gameID);
        }

        var message = String.format("%s is connected as %s.", auth.username(), color.toString());
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, color, message);

        connections.broadcastToGame(gameID, notification);
    }

    private void exit(String authToken, Integer gameID, ChessGame.TeamColor color) throws IOException {
        AuthData authData = authenticate(authToken);

        var message = String.format("%s left the game", authData.username());
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                                                            null, null, message);

        if (connections.checkIsPlayer(gameID, color, authData.username())) {
            connections.removeUser(gameID, color);

            connections.broadcastToGame(gameID, notification);
        } else {
            throw new BadRequestException("not a valid player");
        }
    }

    public void makeMove(
            String authToken,
            int gameID,
            ChessGame.TeamColor color,
            ChessMove move
    ) throws InvalidMoveException, IOException {

        AuthData authData = authenticate(authToken);
        GameData gameData = dataAccess.getGame(gameID);
        ChessGame game = gameData.game();
        if (!(color == game.getBoard().getPiece(move.getStartPosition()).getTeamColor())) {
            throw new InvalidMoveException("looks like you are working for the wrong team");
        }
        game.makeMove(move);
        dataAccess.updateGame(game, gameID);
        var message = String.format("%s calls %s [%s] -> %s!",
                authData.username(),
                game.getBoard().getPiece(move.getStartPosition()).getPieceType(),
                move.getStartPosition().toString(),
                move.getEndPosition().toString()
                );
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game, color, message);
        connections.broadcastToGame(gameID, notification);

    }

    private void resign(String authToken, Integer gameID, ChessGame.TeamColor color) throws IOException, InvalidMoveException {
        // this is a placeholder
        AuthData authData = authenticate(authToken);

        GameData gameData = dataAccess.getGame(gameID);
        ChessGame game = gameData.game();
        game.resignation(color);
        dataAccess.updateGame(game, gameID);

        var message = String.format("%s (%s) resigned the game.", authData.username(), color);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                                                                null, null, message);
        connections.broadcastToGame(gameID, notification); // inform other user of win, and curr user of loss
    }

    public AuthData authenticate(String auth) {
        return service.authenticate(auth);
    }
}

// now I need to make sure that when the client recieves these messages they do the right thing with them.