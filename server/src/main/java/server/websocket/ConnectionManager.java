package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Notification;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
        // the String key is an authToken, and the authToken in Conneciton is the other User's
    public void add(Session session) {
        connections.put(session, session);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcastToGame(String authToken, Notification notification) throws IOException {
        String msg = notification.toString();

        connections.get(authToken);
//        for (Connection c : connections.values()) {
//            if (c.getAuthToken().isOpen()) {
//                if (!c.equals(excludeSession)) {
//                    c.getRemote().sendString(msg);
//                }
//            }
//        }
    }
}
