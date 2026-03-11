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

//    @Test
//    void addUserFail() {
//        UserData user = new UserData("me", "memememe", "me@me.me");
//        Assertions.assertThrows(Exception.class, () -> db.addUser(user));
//    }

    @Test
    void getUserPass() {
        UserData user = new UserData("me", "memememe", "me@me.me");

        UserData rec = db.getUser(user.username());

        Assertions.assertEquals(user, rec);
    }
}
