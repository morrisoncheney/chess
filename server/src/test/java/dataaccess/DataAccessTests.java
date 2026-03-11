package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class DataAccessTests {
    // we shall begin testing
    MySqlDataAccess db;
    UserData unfindable;

    @BeforeEach
    void setup() {
        db = new MySqlDataAccess();
        db.deleteAllUserData();
        db.deleteAllAuthData();
        db.deleteAllGames();
        unfindable = new UserData("Austin Powers", "shallweshagnoworshaglater", "groovy_baby_1967@sis.gov");
    }

    @Test
    void addAndGetUserPass() {
        UserData user = new UserData("me", "memememe", "me@me.me");

        db.addUser(user);

        UserData rec = db.getUser(user.username());

        Assertions.assertEquals(user.email(), rec.email());
    }

    @Test
    void addDuplicateUser() {
        UserData user = new UserData("me", "memememe", "me@me.me");

        db.addUser(user);

        Assertions.assertThrows(Exception.class, () -> db.addUser(user));
    }

    @Test
    void getInternationalManOfMystery(){

        UserData rec = db.getUser(unfindable.username());

        Assertions.assertNull(rec);
    }

    @Test
    void deleteUsersTest() {
        UserData user = new UserData("me1", "memememe1", "me@me.me1");

        db.addUser(user);

        UserData rec = db.getUser(user.username());

        Assertions.assertEquals(user.username(), rec.username());

        db.deleteAllUserData();

        rec = db.getUser(user.username());

        Assertions.assertNull(rec);
    }

    @Test
    void deleteAllNonexistentUsers() {
        Assertions.assertDoesNotThrow(() -> db.deleteAllUserData());
    }

    @Test
    void addAndGetAuthTest(){
        UserData user = new UserData("me1", "memememe1", "me@me.me1");

        db.addUser(user);

        AuthData auth = new AuthData("me1", "67");

        db.addAuth(auth);

        AuthData rec = db.getAuth(auth.authToken());

        Assertions.assertEquals(auth, rec);
    }

    @Test
    void addAuthDupeError() {
        UserData user = new UserData("me1", "memememe1", "me@me.me1");

        db.addUser(user);

        AuthData auth = new AuthData("me1", "67");

        db.addAuth(auth);


        Assertions.assertThrows(Exception.class, () -> db.addAuth(auth));
    }

    @Test
    void getDeletedAuthTest(){
        UserData user = new UserData("me1", "memememe1", "me@me.me1");

        db.addUser(user);

        AuthData auth = new AuthData("me1", "67");

        db.addAuth(auth);

        db.deleteAuth(auth.authToken());

        AuthData rec = db.getAuth(auth.authToken());

        Assertions.assertNull(rec);
    }

    @Test
    void getNonexistentAuth(){
        AuthData auth = db.getAuth("1967_baby_yeah");

        Assertions.assertNull(auth);
    }

    @Test
    void deleteNonexistentAuth(){
        Assertions.assertDoesNotThrow(() -> db.deleteAuth("1967_baby_yeah"));
    }

    @Test
    void deleteAllAuths(){
        UserData user = new UserData("me1", "memememe1", "me@me.me1");

        UserData user2 = new UserData("me12", "memememe12", "me@me.me12");

        db.addUser(user);
        db.addUser(user2);

        AuthData auth = new AuthData("me1", "67");
        AuthData auth2 = new AuthData("me12", "68");

        db.addAuth(auth);
        db.addAuth(auth2);

        AuthData rec = db.getAuth(auth.authToken());

        Assertions.assertEquals(auth, rec);

        db.deleteAllAuthData();

        Assertions.assertNull(db.getAuth("67"));
        Assertions.assertNull(db.getAuth("68"));
    }

    @Test
    void addAndGetGame(){
        int id = db.addGame("gameBoi");

        GameData game = db.getGame(id);

        Assertions.assertEquals("gameBoi", game.gameName());
    }

    @Test
    void getNonexistentGame() {
        GameData game = db.getGame(0);
        Assertions.assertNull(game);
    }

    @Test
    void testGameList() {
        db.addGame("game1");
        db.addGame("game2");

        var gList = db.getGameList();
        Assertions.assertTrue(gList.size() >= 2);
    }

    @Test
    void testGameListEmpty() {
        var gList = db.getGameList();
        Assertions.assertTrue(gList.isEmpty());
    }

    @Test
    void replaceUserTest() {
        int id = db.addGame("game");
        db.replaceUser(chess.ChessGame.TeamColor.WHITE, "user", id);

        GameData game = db.getGame(id);
        Assertions.assertEquals("user", game.whiteUsername());
    }

    @Test
    void replaceNotUserTest() {
        int id = db.addGame("game");

        GameData game = db.getGame(id);
        Assertions.assertNull(game.whiteUsername());
    }

    @Test
    void replaceUserForNonexistentGame() {
        int id = db.addGame("game");
        Assertions.assertThrows(Exception.class,
                () -> db.replaceUser(chess.ChessGame.TeamColor.WHITE, "user", 67));

        GameData game = db.getGame(id);
        Assertions.assertNull(game.whiteUsername());
    }

    @Test
    void deleteGamesTest() {
        db.addGame("el ulti-mo game");
        db.deleteAllGames();

        Assertions.assertEquals(0, db.getGameList().size());
    }
}
