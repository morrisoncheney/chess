package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    ChessGame game = null;
    ChessGame.TeamColor color;
    String message;
    String errorMessage;

    public ServerMessage(ServerMessageType type, ChessGame game, ChessGame.TeamColor color, String message) {
        this.serverMessageType = type;
        this.game = game;
        this.color = color;
        this.message = message;
    }

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION,
        ENTER
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public ChessGame getGame() {
        return game;
    }

    public ChessGame.TeamColor getColor() {
        return this.color;
    }

    public String getMsg() {
        return message;
    }

    public void setErrorMessage() {
        this.errorMessage = this.message;
        this.message = null;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
