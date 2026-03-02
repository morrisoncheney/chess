package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.GameDataListItem;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryDataAccess {
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<String, AuthData> auths = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();

    private Integer nextId = 1233;

    public UserData addUser(UserData user) {

        users.put(user.username(), user);

        return user;
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public void deleteUser(String username) {
        users.remove(username);
    }

    public void deleteAllUserData() { users.clear(); }

/////////////////////////////////////////////////////////////////////

    public void addAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);

    }

    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    public void deleteAllAuthData() {
        auths.clear();
    }

    ///////////////////////////////////////////////////////////////

    public Integer addGame(String gameName) {
        nextId++;
        GameData game = new GameData(nextId, new ChessGame(), "null", "null", gameName);

        games.put(nextId, game);

        return nextId;
    }

    public GameData getGame(Integer gameId) {
        return games.get(gameId);
    }

    public ArrayList<GameDataListItem> getGameList(){

        ArrayList<GameDataListItem> gameList = new ArrayList<>();

        for (GameData game : games.values()) {
            GameDataListItem item;
            item = new GameDataListItem(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName());
            gameList.add(item);
        }
        return gameList;

    }

    public void replaceUser(ChessGame.TeamColor color, String username, Integer gameID) {
        GameData data = games.get(gameID);

        GameData newGame;
        if (color == ChessGame.TeamColor.WHITE){
            newGame = new GameData(data.gameID(), data.game(), username, data.blackUsername(), data.gameName());
            games.replace(gameID, newGame);
        } else {
            newGame = new GameData(data.gameID(), data.game(), data.whiteUsername(), username, data.gameName());
            games.replace(gameID, newGame);
        }
    }

    public void deleteGame(Integer gameId) {
        games.remove(gameId);
    }

    public void deleteAllGames() {
        games.clear();
    }

}
