package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.MySqlDataAccess;
import model.BadRequestException;
import model.GameData;
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
        try {
            switch (action.getCommandType()) {
                case CONNECT -> enter(action.getAuthToken(), action.getGameID(), ctx.session);
                case MAKE_MOVE -> makeMove(action.getAuthToken(), action.getGameID(), action.getMove(), ctx.session);
                case LEAVE -> exit(action.getAuthToken(), action.getGameID(), ctx.session);
                case RESIGN -> resign(action.getAuthToken(), action.getGameID(), ctx.session);
            }
        } catch (Exception e) {
            ServerMessage error = new ServerMessage(
                    ServerMessage.ServerMessageType.ERROR,
                    null,
                    null,
                    e.getMessage()
            );
            error.setErrorMessage();
            connections.broadcastToSession(ctx.session, error);
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        connections.removeSession(ctx.session);
        System.out.println("Websocket closed.");
    }

    private void enter(String authToken, int gameID, Session session) throws IOException {
        connections.add(new Connection(gameID));

        AuthData authData = authenticate(authToken);
        ChessGame.TeamColor color = getColor(gameID, authData);

        connections.addUser(gameID, authData.username(), color, session);
        ChessGame game = dataAccess.getGame(gameID).game();
        String itemTwo;
        if (color == null) {
            itemTwo = "observer";
        } else {
            itemTwo = color.toString();
        }
        String message = String.format("%s is connected as %s.", authData.username(), itemTwo);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game, color, null);

        connections.broadcastToSession(session, notification);

        notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, color, message);
        connections.broadcastWithExclusion(session, gameID, notification);
    }

    private void exit(String authToken, Integer gameID, Session session) throws IOException {
        AuthData authData = authenticate(authToken);
        ChessGame.TeamColor color = getColor(gameID, authData);

        var message = String.format("%s left the game", authData.username());
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                                                            null, null, message);
        connections.removeSession(session);
        connections.broadcastWithExclusion(session, gameID, notification);
    }

    public void makeMove(
            String authToken,
            int gameID,
            ChessMove move,
            Session session
    ) throws InvalidMoveException, IOException {

        AuthData authData = authenticate(authToken);
        ChessGame.TeamColor color = getColor(gameID, authData);

        if (color == null) {
            throw new InvalidMoveException("observer cannot make moves.");
        }

        GameData gameData = dataAccess.getGame(gameID);
        ChessGame game = gameData.game();

        if (game.getTeamTurn() == null) {
            throw new InvalidMoveException("game already over.");
        }

        if (!(color == game.getBoard().getPiece(move.getStartPosition()).getTeamColor())) {
            throw new InvalidMoveException("looks like you are working for the wrong team");
        }

        game.makeMove(move);
        String extra = "";
        boolean gameOver = false;
        ChessGame.TeamColor opponent = (color == ChessGame.TeamColor.BLACK) ? ChessGame.TeamColor.WHITE
                                                                                        : ChessGame.TeamColor.BLACK;
        if (game.isInCheckmate(opponent)) {
            gameOver = true;
            extra = String.format(" Checkmate! %s wins!", color.toString());
            game.gameComplete();
        } else if (game.isInStalemate(opponent)) {
            gameOver = true;
            extra = " Stalemate!";
            game.gameComplete();
        } else if (game.isInCheck(opponent)) {
            extra = "Check!";
        }
        dataAccess.updateGame(game, gameID);

        var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game, color, null);
        connections.broadcastToGame(gameID, loadGame);
        var message = String.format("%s calls %s [%s] -> %s!",
                authData.username(),
                game.getBoard().getPiece(move.getEndPosition()).getPieceType(),
                move.getStartPosition().toString(),
                move.getEndPosition().toString()
        );
        if (!gameOver) {
            message = message + extra;
        }
        ServerMessage notification = new ServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                null,
                null,
                message
        );

        connections.broadcastWithExclusion(session, gameID, notification);
        if (gameOver) {
            notification = new ServerMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    null,
                    null,
                    extra
            );
            connections.broadcastToGame(gameID, notification);
        }
    }

    private void resign(String authToken, Integer gameID, Session session) throws IOException, InvalidMoveException {

        AuthData authData = authenticate(authToken);
        ChessGame.TeamColor color = getColor(gameID, authData);

        if (color == null) {
            throw new InvalidMoveException("observer cannot resign");
        }

        GameData gameData = dataAccess.getGame(gameID);
        ChessGame game = gameData.game();

        if (game.getTeamTurn() == null) {
            throw new InvalidMoveException("game already over.");
        } // this should not be needed.

        game.gameComplete();
        dataAccess.updateGame(game, gameID);

        var message = String.format("%s (%s) resigned the game.", authData.username(), color);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                                                                null, null, message);
        connections.broadcastToGame(gameID, notification); // inform other user of win, and curr user of loss
    }

    private AuthData authenticate(String auth) {
        return service.authenticate(auth);
    }

    private ChessGame.TeamColor getColor(Integer gameID, AuthData auth) {
        GameData data = dataAccess.getGame(gameID);
        if (data == null) {
            throw new BadRequestException("this game doesn't exist.");
        }
        System.out.println(auth);
        System.out.println(data);
         if (data.blackUsername().equals(auth.username())) {
            return ChessGame.TeamColor.BLACK;
        } else if (data.whiteUsername().equals(auth.username())) {
            return  ChessGame.TeamColor.WHITE;
        } else {
             return null;
         }
    }
}

// now I need to make sure that when the client receives these messages they do the right thing with them.