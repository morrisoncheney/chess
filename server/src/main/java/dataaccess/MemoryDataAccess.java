package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryDataAccess {
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<String, AuthData> auths = new HashMap<>();
    final private HashMap<String, GameData> games = new HashMap<>();

    private int nextId = 1234;

    public UserData addUser(UserData user) {
        user = new UserData(user.username(), user.password(), user.email());

        users.put(user.username(), user);
        return user;
    }

//    public PetList listPets() {return new PetList(users.values());}
//    I don't think I need a UserList or anything like that.

    public UserData getUser(String username) {
        return users.get(username);
    }

    public void deleteUser(String username) {
        users.remove(username);
    }

    public void deleteAllUserData() {
        users.clear();
    }

/////////////////////////////////////////////////////////////////////

    public AuthData addAuth(AuthData auth) {
        auth = new AuthData(auth.authToken(), auth.username());

        auths.put(auth.authToken(), auth);
        return auth;
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

    public int addGame() {

        return 67;
    }

    public GameData getGame(String gameId) {
        return games.get(gameId);
    }

    public ArrayList<String> getGameList(){

        ArrayList<String> gameList = new ArrayList<>();

        for (GameData game : games.values()) {
            gameList.add(game.toJsonString());
        }
        return gameList;

    }

    public void updateGame(String gameID, ChessGame game) {

        games.repalce(gameID, games.get(gameID).update(game));

    }

    public void deleteGame(String gameId) {
        games.remove(gameId);
    }

    public void deleteAllGames() {
        games.clear();
    }

}
