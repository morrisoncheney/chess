package client.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand; // replace Action
import websocket.messages.ServerMessage; // replace Notification

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void enter(String authToken, int gameID, ChessGame.TeamColor color) throws ResponseException {
        try {
            UserGameCommand action = new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT,
                    authToken,
                    gameID
            );
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void exit(String authToken, int gameID, ChessGame.TeamColor color) throws ResponseException {
        try {
            UserGameCommand action = new UserGameCommand(
                    UserGameCommand.CommandType.LEAVE,
                    authToken,
                    gameID
            );
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move, ChessGame.TeamColor color) throws ResponseException {
        try {
            UserGameCommand action = new UserGameCommand(
                    UserGameCommand.CommandType.MAKE_MOVE,
                    authToken,
                    gameID,
                    move
            );
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID, ChessGame.TeamColor color) throws ResponseException {
        try {
            UserGameCommand action = new UserGameCommand(
                    UserGameCommand.CommandType.RESIGN,
                    authToken,
                    gameID
            );
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

}

