package service;

import dataaccess.MemoryDataAccess;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.LoginRequest;
import model.LogoutRequest;
import model.UserData;
import java.util.UUID;


public class Service {

    private final MemoryDataAccess dataAccess;

    public Service(MemoryDataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData registerRequest(UserData user){
        user.check();

        checkForUser(user); // Throws an error if exists.

        dataAccess.addUser(user);

        String authToken = generateToken();

        AuthData auth = new AuthData(user.username(), authToken);

        dataAccess.addAuth(auth);

        return auth;
    }

    public AuthData loginRequest(LoginRequest log) {
        log.check();

        UserData currentUser = dataAccess.getUser(log.username());
        if (currentUser == null) {
            throw new UnauthorizedResponse("unauthorized");
        }

        String authToken = generateToken();

        AuthData auth = new AuthData(log.username(), authToken);

        dataAccess.addAuth(auth);

        return auth;
    }

    public void logoutRequest(LogoutRequest log) {
        log.check();

        AuthData currAuth = dataAccess.getAuth(log.authToken());
        if (currAuth == null) {
            throw new UnauthorizedResponse("unauthorized");
        }

        dataAccess.deleteAuth(log.authToken());

    }

    public void checkForUser(UserData user){
        UserData currentUser = dataAccess.getUser(user.username());
        if (currentUser != null) {
            throw new ForbiddenResponse("already taken");
        }
    }

    public void authenticate(String authToken) throws Exception {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedResponse("unauthorized");
        }
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
