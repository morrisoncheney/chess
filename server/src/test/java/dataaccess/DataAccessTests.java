package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class DataAccessTests {
    // we shall begin testing
    static MySqlDataAccess db;

    @BeforeAll
    static void setup() {
        db = new MySqlDataAccess();
        db.deleteAllUserData();
    }

    @Test
    void addAndGetUserPass() {
        UserData user = new UserData("me", "memememe", "me@me.me");

        db.addUser(user);

        UserData rec = db.getUser(user.username());

        Assertions.assertEquals(user.email(), rec.email());
    }

    @Test
    void getUserPass() {
        UserData user = new UserData("me1", "memememe1", "me@me.me1");

        db.addUser(user);

        UserData rec = db.getUser(user.username());

        Assertions.assertEquals(user.username(), rec.username());
    }

    @Test
    void getInternationalManOfMystery(){
        UserData unfindable = new UserData("Austin Powers", "shallweshagnoworshaglater", "groovy_baby_1967@sis.gov");

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



}
