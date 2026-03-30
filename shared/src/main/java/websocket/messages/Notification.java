package websocket.messages;

import com.google.gson.Gson;

public record Notification(Type type, String message) {
    public enum Type {
        ENTER,
        MOVE,
        RESIGNATION,
        EXIT
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
