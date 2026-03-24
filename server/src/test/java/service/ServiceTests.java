package service;

import chess.ChessGame;
import dataaccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.LoginRequest;
import model.UserData;
import model.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTests {

    private Service service;
    private MySqlDataAccess data;

    @BeforeEach
    void setup() {
        data = new MySqlDataAccess();
        service = new Service(data);
    }

    @Test
    void registerAndLoginLogout() {
        UserData user = new UserData("x", "y", "z");
        AuthData auth = service.registerRequest(user);
        assertEquals("x", auth.username());
        assertNotNull(data.getAuth(auth.authToken()), "token should be stored after register");

        LoginRequest login = new LoginRequest("x", "y");
        AuthData loginAuth = service.loginRequest(login);
        assertEquals("x", loginAuth.username());
        assertNotNull(data.getAuth(loginAuth.authToken()));

        service.logoutRequest(auth.authToken());
        assertNull(data.getAuth(auth.authToken()), "token should be removed after logout");
    }

    @Test
    void registerDuplicateThrowsForbidden() {
        UserData user = new UserData("x", "y", "z");
        service.registerRequest(user);
        assertThrows(ForbiddenResponse.class, () -> service.registerRequest(user));
    }

    @Test
    void loginWrongPasswordThrowsUnauthorized() {
        UserData user = new UserData("x2", "y2", "z2");
        service.registerRequest(user);
        LoginRequest bad = new LoginRequest("x2", "wrong");
        assertThrows(UnauthorizedResponse.class, () -> service.loginRequest(bad));
    }

    @Test
    void loginNonexistentThrowsUnauthorized() {
        LoginRequest bad = new LoginRequest("nosuch", "whatever");
        assertThrows(UnauthorizedResponse.class, () -> service.loginRequest(bad));
    }

    @Test
    void logoutInvalidTokenThrowsUnauthorized() {
        assertThrows(UnauthorizedResponse.class, () -> service.logoutRequest("nope"));
    }

    @Test
    void logoutEmptyOrNullTokenThrowsBadRequest() {
        assertThrows(BadRequestException.class, () -> service.logoutRequest(""));
        assertThrows(BadRequestException.class, () -> service.logoutRequest(null));
    }

    @Test
    void findUserAndAuthenticate() {
        UserData u = new UserData("x3", "y3", "z3");
        service.registerRequest(u);
        UserData found = service.findUser("x3");
        assertEquals(u.username(), found.username());

        AuthData auth = data.getAuth(service.loginRequest(new LoginRequest("x3", "y3")).authToken());

        assertDoesNotThrow(() -> service.authenticate(auth.authToken()));

        assertThrows(UnauthorizedResponse.class, () -> service.authenticate("badtoken"));
    }

    @Test
    void registerGood() {
        UserData user = new UserData("x","y","z");
        service.registerRequest(user);

        UserData found = service.findUser("x");

        assertEquals(user, found);
    }

    @Test
    void registerBad() {
        UserData user = new UserData("x","","");

        ;assertThrows(Exception.class, () -> service.registerRequest(user));

    }

    @Test
    void loginGood() {
        UserData user = new UserData("x", "y", "z");
        service.registerRequest(user);
        LoginRequest login = new LoginRequest("x", "y");
        AuthData auth = service.loginRequest(login);
        assertEquals("x", auth.username());
        assertNotNull(data.getAuth(auth.authToken()), "auth token should be stored after login");
    }

    @Test
    void loginBad() {
        UserData user = new UserData("x", "y", "z");
        service.registerRequest(user);
        LoginRequest badPassword = new LoginRequest("x", "wrongpassword");
        assertThrows(UnauthorizedResponse.class, () -> service.loginRequest(badPassword));
        
        LoginRequest badUser = new LoginRequest("nonexistent", "y");
        assertThrows(UnauthorizedResponse.class, () -> service.loginRequest(badUser));
    }

    @Test
    void logoutGood() {
        UserData user = new UserData("x5", "y5", "z5");
        AuthData auth = service.registerRequest(user);
        assertNotNull(data.getAuth(auth.authToken()), "token should exist after register");
        
        service.logoutRequest(auth.authToken());
        assertNull(data.getAuth(auth.authToken()), "token should be deleted after logout");
    }

    @Test
    void logoutBad() {
        UserData user = new UserData("x6", "y6", "z6");
        AuthData auth = service.registerRequest(user);
        service.logoutRequest(auth.authToken());
        
        assertThrows(UnauthorizedResponse.class, () -> service.logoutRequest(auth.authToken()));
    }

    @Test
    void createGameGood() {
        Integer gameID = service.createGame("MyGame");
        assertNotNull(gameID, "game ID should be generated");
        
        GameData game = data.getGame(gameID);
        assertNotNull(game, "game should be stored in data access");
        assertEquals("MyGame", game.gameName());
    }

    @Test
    void createGameBad() {

        Integer gameID1 = service.createGame("Game1");
        Integer gameID2 = service.createGame("Game2");
        
        assertNotEquals(gameID1, gameID2, "each game should have a unique ID");
        assertEquals("Game1", data.getGame(gameID1).gameName());
        assertEquals("Game2", data.getGame(gameID2).gameName());
    }

    @Test
    void joinGood() {
        UserData user = new UserData("player1", "pass", "email");
        service.registerRequest(user);
        
        Integer gameID = service.createGame("TestGame");
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID);
        
        assertDoesNotThrow(() -> service.joinGame(request, "player1"));
        
        GameData game = data.getGame(gameID);
        assertEquals("player1", game.whiteUsername());
    }

    @Test
    void joinBad() {
        UserData user1 = new UserData("player1", "pass", "email");
        UserData user2 = new UserData("player2", "pass", "email");
        service.registerRequest(user1);
        service.registerRequest(user2);
        
        Integer gameID = service.createGame("TestGame");

        JoinGameRequest whiteRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID);
        service.joinGame(whiteRequest, "player1");

        assertThrows(ForbiddenResponse.class, () -> service.joinGame(whiteRequest, "player2"));
    }

    @Test
    void checkUserDNEGood() {
        UserData user = new UserData("newuser", "password", "email@example.com");

        assertDoesNotThrow(() -> service.checkUserDNE(user));
    }

    @Test
    void checkUserDNEBad() {
        UserData user = new UserData("testuser", "password", "email");
        service.registerRequest(user);

        assertThrows(ForbiddenResponse.class, () -> service.checkUserDNE(user));
    }

    @Test
    void findUserGood() {
        UserData user = new UserData("findme", "pass123", "find@email.com");
        service.registerRequest(user);
        
        UserData found = service.findUser("findme");
        assertEquals(user, found);
        assertEquals("findme", found.username());
    }

    @Test
    void findUserBad() {

        assertThrows(UnauthorizedResponse.class, () -> service.findUser("nonexistent"));
    }

    @Test
    void authenticateGood() {
        UserData user = new UserData("authuser", "pass", "email");
        AuthData auth = service.registerRequest(user);
        
        AuthData authenticated = service.authenticate(auth.authToken());
        assertNotNull(authenticated);
        assertEquals(auth.username(), authenticated.username());
        assertEquals(auth.authToken(), authenticated.authToken());
    }

    @Test
    void authenticateBad() {

        assertThrows(UnauthorizedResponse.class, () -> service.authenticate("invaltoken123"));

        assertThrows(UnauthorizedResponse.class, () -> service.authenticate(null));
        assertThrows(UnauthorizedResponse.class, () -> service.authenticate(""));
    }

    @Test
    void memorySelfDestructGood() {
        UserData u = new UserData("x4", "y4", "z4");
        AuthData auth = service.registerRequest(u);
        assertNotNull(data.getUser("x4"));
        assertNotNull(data.getAuth(auth.authToken()));

        service.memorySelfDestruct();
        assertNull(data.getUser("x4"));
        assertNull(data.getAuth(auth.authToken()));
    }

    @Test
    void memorySelfDestructBad() {
        service.memorySelfDestruct();
    }


}

