package server.websocket;
import model.BadRequestException;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;

public class Connection {
    public Session whiteSession;  // or username
    public String whiteUsername;
    public Session blackSession;
    public String blackUsername;
    public int gameID;
    public ArrayList<Session> observers = new ArrayList<>();

    public Connection(int gameID) {
        this.gameID = gameID;
    }

    public void setWhite(String username, Session s) {
        this.whiteUsername = username;
        this.whiteSession = s;
    }

    public void setBlack(String username, Session s) {
        this.blackUsername = username;
        this.blackSession = s;
    }

    public void addObserver(Session s) {
        if (observers.contains(s)) {
            throw new BadRequestException("already observing this game");
        } else {
            observers.add(s);
        }
    }

    public Session getWhiteSession() {
        return whiteSession;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public Session getBlackSession() {
        return blackSession;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public int getGameID() {
        return gameID;
    }

    public ArrayList<Session> getObservers() {
        return observers;
    }
}
