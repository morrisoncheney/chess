package service;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.MemoryDataAccess;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.*;
import server.BadRequestException;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;


public class Service {

    private final MemoryDataAccess dataAccess;

    public Service(MemoryDataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData registerRequest(UserData user){
        user.check();

        checkUserDNE(user); // Throws an error if exists.

        dataAccess.addUser(user);

        String authToken = generateToken();

        AuthData auth = new AuthData(user.username(), authToken);

        dataAccess.addAuth(auth);

        return auth;
    }

    public AuthData loginRequest(LoginRequest log) {
        log.check();

        UserData user = findUser(log.username());

        if (!Objects.equals(user.password(), log.password())){
            throw new UnauthorizedResponse("unauthorized");
        }

        String authToken = generateToken();

        AuthData auth = new AuthData(log.username(), authToken);

        dataAccess.addAuth(auth);

        return auth;
    }

    public void logoutRequest(String auth) {
        try {
            if (auth.isEmpty()) {
                throw new Exception("sugma");
            }
        } catch (Exception e) {
            throw new BadRequestException("bad request");
        }

        authenticate(auth);

        dataAccess.deleteAuth(auth);

    }

    public Integer createGame(String gameName){
        return dataAccess.addGame(gameName);
    }

    public ArrayList<GameDataListItem> getGameList() {
        return dataAccess.getGameList();
    }

    public void joinGame(JoinGameRequest request, String username) {
        Integer gameID = request.gameID();

        GameData game = dataAccess.getGame(gameID);

        if (request.playerColor() == ChessGame.TeamColor.WHITE && game.whiteUsername() == null) {
            dataAccess.replaceUser(request.playerColor(), username, gameID);
        } else if (game.blackUsername() == null) {
            dataAccess.replaceUser(request.playerColor(), username, gameID);
        } else {
            throw new ForbiddenResponse("already taken");
        }
    }

    public void checkUserDNE(UserData user){
        UserData currentUser = dataAccess.getUser(user.username());
        if (currentUser != null) {
            throw new ForbiddenResponse("already taken");
        }
    }

    public UserData findUser(String username){
        UserData currentUser = dataAccess.getUser(username);
        if (currentUser == null) {
            throw new UnauthorizedResponse("unauthorized");
        }
        return currentUser;
    }

    public AuthData authenticate(String authToken) throws UnauthorizedResponse {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedResponse("unauthorized");
        }
        return auth;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public Integer memorySelfDestruct(){

        dataAccess.deleteAllUserData();
        dataAccess.deleteAllAuthData();
        dataAccess.deleteAllGames();
        
        return null;
    }



}
