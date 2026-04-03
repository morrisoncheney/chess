package server.websocket;

import chess.ChessGame;
import model.BadRequestException;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import org.eclipse.jetty.websocket.api.Session;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Connection> connections = new ConcurrentHashMap<>();
        // the String key is a white, and the white in Connection is the other User's
    public void add(Connection c) {
        connections.putIfAbsent(c.getGameID(), c);
    }

    public boolean checkIsPlayer(Integer gameID, ChessGame.TeamColor color, String username) {
        if (color == ChessGame.TeamColor.WHITE) {
            return connections.get(gameID).getWhiteUsername().equals(username);
        } else if (color == ChessGame.TeamColor.BLACK) {
            return connections.get(gameID).getBlackUsername().equals(username);
        } else {
            throw new BadRequestException("color can't be null you bot");
        }
    }

    public void addUser(Integer gameID, String username, ChessGame.TeamColor color, Session session) {
        Connection c = connections.get(gameID);
        if (color == ChessGame.TeamColor.WHITE) {
            c.setWhite(username, session);
        } else if (color == ChessGame.TeamColor.BLACK) {
            c.setBlack(username, session);
        } else if (color == null) {
            c.addObserver(session);
        }
        connections.put(gameID, c);
    }

    public void removeUser(Integer gameID, ChessGame.TeamColor color) {
        Connection c = connections.get(gameID);
        if (color == ChessGame.TeamColor.WHITE) {
            c.setWhite(null, null);
        } else if (color == ChessGame.TeamColor.BLACK) {
            c.setBlack(null, null);
        }
        connections.put(gameID, c);
    }

    public void broadcastToGame(Integer gameID, ServerMessage notification) throws IOException {
        String msg = notification.toString();
        Session s;
        Connection c = connections.get(gameID);
        s = c.getWhiteSession();
        if (s != null) {
            s.getRemote().sendString(msg);
        }
        s = c.getBlackSession();
        if (s != null) {
            s.getRemote().sendString(msg);
        }

        ArrayList<Session> list1 = new ArrayList<>(c.getObservers());

        for (Session sesh : list1) {
            if (sesh != null) {
                sesh.getRemote().sendString(msg);
            }
        }
    }

    public void broadcastWithExclusion(Session exclude, Integer gameID, ServerMessage notification) throws IOException {
        System.out.println(notification.getMsg());
        String msg = notification.toString();
        Session s;
        Connection c = connections.get(gameID);
        s = c.getWhiteSession();
        if (s != null && s != exclude) {
            s.getRemote().sendString(msg);
        }
        s = c.getBlackSession();
        if (s != null && s != exclude) {
            s.getRemote().sendString(msg);
        }

        ArrayList<Session> list1 = new ArrayList<>(c.getObservers());

        for (Session sesh : list1) {
            if (sesh != null && sesh != exclude) {
                sesh.getRemote().sendString(msg);
            }
        }
    }

    public void broadcastToSession(Session s, ServerMessage notification) throws IOException {
        String msg = notification.toString();
        s.getRemote().sendString(msg);
    }

    public void removeSession(Session session) {
        for (Connection c : connections.values()) {
            if (session.equals(c.getWhiteSession())) {
                c.setWhite(null, null);
            } else if (session.equals(c.getBlackSession())) {
                c.setBlack(null, null);
            } else {
                c.getObservers().remove(session);
            }
        }
    }
}
