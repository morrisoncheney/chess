package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class DataAccessTests {
    // we shall begin testing
    static MySqlDataAccess db;
    static UserData unfindable;

    @BeforeAll
    static void setup() {
        db = new MySqlDataAccess();
        db.deleteAllUserData();
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
    void addAndGetAuthTest(){
        UserData user = new UserData("me1", "memememe1", "me@me.me1");

        db.addUser(user);

        AuthData auth = new AuthData("me1", "67");

        db.addAuth(auth);

        AuthData rec = db.getAuth(auth.username());

        Assertions.assertEquals(auth, rec);
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
    void deleteNonexistentAuth(){
        db.getAuth("1967_baby_yeah");
    }

}
